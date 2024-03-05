package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.iotstar.jobhub_hcmute_be.constant.PostEmotions;
import vn.iotstar.jobhub_hcmute_be.dto.CreatePostRequestDTO;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.enums.PrivacyLevel;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.GetPostsModel;
import vn.iotstar.jobhub_hcmute_be.model.PageModel;
import vn.iotstar.jobhub_hcmute_be.repository.*;
import vn.iotstar.jobhub_hcmute_be.service.CloudinaryService;
import vn.iotstar.jobhub_hcmute_be.service.PostService;
import vn.iotstar.jobhub_hcmute_be.utils.CurrentUserUtils;

import java.io.IOException;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private LikeRepository likeRepository;
    //Ham kiểm tra xem user có thich post chưa
    private PostEmotions checkIfLikeAndGetEmotions(User user, Post post) {
        Optional<Like> like = likeRepository.findByPostAndUser(post, user);
        if (like.isPresent()) {
            return like.get().getEmotions();
        }
        return null;
    }
    private GetPostsModel transform (Post post){
        try {
            Optional<User> userCurrent = userRepository.findById(CurrentUserUtils.getCurrentUserId());
            String userId = post.getUser().getUserId();
            Optional<User> user = userRepository.findById(userId);
            Optional<Student> student = studentRepository.findById(userId);
            Optional<Employer> employer = employerRepository.findById(userId);
            if (student.isPresent()) {
                return GetPostsModel.transform(post, student.get().getFullName(), student.get().getAvatar(), checkIfLikeAndGetEmotions(userCurrent.get(), post));
            }
            if (employer.isPresent()) {
                return GetPostsModel.transform(post, employer.get().getCompanyName(), employer.get().getLogo(), checkIfLikeAndGetEmotions(userCurrent.get(), post));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private List<GetPostsModel> transformList (List<Post> posts){
        List<GetPostsModel> getPostsModels = new ArrayList<>();
        for (Post post : posts) {
            getPostsModels.add(transform(post));
        }
        return getPostsModels;
    }
    private Boolean checkIfFriend(User user1, User user2) {
        List<FriendUser> friends = user1.getFriends();
        for (FriendUser friend : friends) {
            if (friend.getFriendId().equals(user2.getUserId())) {
                return true;
            }
        }
        return false;
    }
    private Boolean checkIfFollow(User user1, User user2) {
        List<Follower> followings = user1.getFollowing();
        for (Follower following : followings) {
            if (following.getFollowee().getUserId().equals(user2.getUserId())) {
                return true;
            }
        }
        return false;
    }

    private List<User> listFriendIds(User user) {
        List<FriendUser> friends = user.getFriends();
        List<String> friendIds = friends.stream().map(friend -> friend.getFriendId()).toList();
        List<User> friendUsers = userRepository.findByUserIdIn(friendIds);
        return friendUsers;
    }

    private List<User> listFollowIds(User user) {
        List<User> followUsers = user.getFollowing().stream().map(friend -> friend.getFollowee()).toList();
        return followUsers;
    }

    private ActionResult getPostsByUserIdHandle (User user, Integer page, Integer size, List<PrivacyLevel> privacyLevels){
        ActionResult actionResult = new ActionResult();
        try {
            Page<Post> posts = postRepository.findByUserAndPrivacyLevelInOrderByCreatedAtDesc(user, privacyLevels, PageRequest.of(page - 1, size));
            actionResult.setData(PageModel.transform(posts, transformList(posts.getContent())));
            actionResult.setErrorCode(ErrorCodeEnum.GET_POST_SUCCESS);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult getPostsByUserId(String userId, Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> userCurrent = userRepository.findById(CurrentUserUtils.getCurrentUserId());
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                return actionResult;
            }
            if(checkIfFriend(userCurrent.get(), user.get())){
                return getPostsByUserIdHandle(user.get(), page, size, Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS));
            }
            else if (userCurrent.get().getUserId().equals(user.get().getUserId())){
                return getPostsByUserIdHandle(user.get(), page, size, Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS, PrivacyLevel.PRIVATE));
            }
            else {
                return getPostsByUserIdHandle(user.get(), page, size, Arrays.asList(PrivacyLevel.PUBLIC));
            }
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult getListNewPosts(Integer page, Integer size, int type){
        return type == 1 ? getListNewPostsFriend(page, size) : getListNewPostsFollow(page, size);
    }

    private ActionResult getListNewPostsFollow(Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        Optional<User> userCurrent = userRepository.findById(CurrentUserUtils.getCurrentUserId());
        try {
            Set<User> allFollowUsers = new HashSet<>();
            allFollowUsers.addAll(listFollowIds(userCurrent.get()));
            Page<Post> postsFollow = postRepository.findByUserInAndPrivacyLevelInOrderByCreatedAtDesc(allFollowUsers, Arrays.asList(PrivacyLevel.PUBLIC), PageRequest.of(page - 1, size));
            actionResult.setData(PageModel.transform(postsFollow, transformList(postsFollow.getContent())));
            actionResult.setErrorCode(ErrorCodeEnum.GET_POST_SUCCESS);

        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }
    private ActionResult getListNewPostsFriend(Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        Optional<User> userCurrent = userRepository.findById(CurrentUserUtils.getCurrentUserId());
        try {
            Set<User> allFriendsUsers = new HashSet<>();
            allFriendsUsers.addAll(listFriendIds(userCurrent.get()));
            Page<Post> posts = postRepository.findByUserInAndPrivacyLevelInOrderByCreatedAtDesc(allFriendsUsers, Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS), PageRequest.of(page - 1, size));
//            Set<Post> allPosts = new HashSet<>();
//            allPosts.addAll(posts.getContent());
//            allPosts.addAll(postsFollow.getContent());
//            List<Post> listPosts = allPosts.stream().sorted(Comparator.comparing(Post::getCreatedAt).reversed()).toList();
//            int totalPage = posts.getTotalPages() + postsFollow.getTotalPages();
//            long totalPost = posts.getTotalElements() + postsFollow.getTotalElements();
//            int currentPage = page - 1;
            actionResult.setData(PageModel.transform(posts, transformList(posts.getContent())));
            actionResult.setErrorCode(ErrorCodeEnum.GET_POST_SUCCESS);

        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }
    @Override
    public ActionResult getImgagesAndVideoAndFile(Integer postId, Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        try {
            if (postId == null) {
                actionResult.setErrorCode(ErrorCodeEnum.POST_REQUIRED_FIELDS);
                return actionResult;
            }
            Optional<Post> post = postRepository.findById(postId);
            if (post.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.POST_NOT_FOUND);
                return actionResult;
            }

            Page<Photo> photos = photoRepository.findByPost(post.get(), PageRequest.of(page - 1, size));
            Map<String, Object> response = new HashMap<>();
            response.put("photos", PageModel.transform(photos));
            response.put("file", post.get().getFiles());
            actionResult.setErrorCode(ErrorCodeEnum.GET_POST_SUCCESS);
            actionResult.setData(response);
        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    private List<Photo> uploadPhotos(CreatePostRequestDTO createPostRequestDTO, Post post) {
        List<String> photoUrls = createPostRequestDTO.getPhotos().stream().map(photo -> {
            try {
                if (photo.getContentType().startsWith("image/")) {
                    return cloudinaryService.uploadImage(photo);
                } else if (photo.getContentType().startsWith("video/")) {
                    return cloudinaryService.uploadVideo(photo);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).toList();
        List<Photo> photos = photoUrls.stream().map(photoUrl -> {
            Photo photo = new Photo();
            photo.setPhotoUrl(photoUrl);
            photo.setPost(post);
            return photo;
        }).toList();
        return photos;
    }

    @Override
    public ActionResult createUserPost(CreatePostRequestDTO createPostRequestDTO) {
        ActionResult actionResult = new ActionResult();
        try {
            String userCreate = CurrentUserUtils.getCurrentUserId();
            Optional<User> userCreatePost = userRepository.findById(userCreate);
            if (userCreatePost.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                return actionResult;
            }
            Post post = new Post();
            post.setHaveFiles(0);
            post.setHavePictures(0);
            BeanUtils.copyProperties(createPostRequestDTO, post);
            post.setUser(userCreatePost.get());
            if (createPostRequestDTO.getPhotos() != null && createPostRequestDTO.getPhotos().size() > 0) {
                post.setHavePictures(createPostRequestDTO.getPhotos().size());
                if (createPostRequestDTO.getPhotos().size() > 15) {
                    actionResult.setErrorCode(ErrorCodeEnum.MAXIMUM_PHOTO);
                    return actionResult;
                }
                List<Photo> photos = uploadPhotos(createPostRequestDTO, post);
                post.setPhotos(photos);
            }

            if (createPostRequestDTO.getFiles() != null && createPostRequestDTO.getFiles().getContentType() != null) {
                List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");
                String fileExtension = StringUtils.getFilenameExtension(createPostRequestDTO.getFiles().getOriginalFilename());// .docx
                if (!allowedFileExtensions.contains(fileExtension)) {
                    actionResult.setErrorCode(ErrorCodeEnum.FILE_EXTENSION_NOT_ALLOWED);
                    return actionResult;
                }
                post.setHaveFiles(1);
                post.setFiles(cloudinaryService.uploadFile(createPostRequestDTO.getFiles()));
            }
            save(post);
            actionResult.setErrorCode(ErrorCodeEnum.CREATE_POST_SUCCESS);
            actionResult.setData(transform(post));
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult updatePost(CreatePostRequestDTO updatePostDTO, Integer postId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.POST_NOT_FOUND);
                return actionResult;
            }
            if (!post.get().getUser().getUserId().equals(CurrentUserUtils.getCurrentUserId())) {
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_PERMISSION);
                return actionResult;
            }
            BeanUtils.copyProperties(updatePostDTO, post.get());
            if (updatePostDTO.getPhotos() != null && updatePostDTO.getPhotos().size() > 0) {
                if (updatePostDTO.getPhotos().size() > 15) {
                    actionResult.setErrorCode(ErrorCodeEnum.MAXIMUM_PHOTO);
                    return actionResult;
                }
                post.get().setHavePictures(updatePostDTO.getPhotos().size());
                List<Photo> photos = uploadPhotos(updatePostDTO, post.get());
                post.get().setPhotos(photos);
            }
            if (updatePostDTO.getFiles() != null && updatePostDTO.getFiles().getContentType() != null) {
                List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");
                String fileExtension = StringUtils.getFilenameExtension(updatePostDTO.getFiles().getOriginalFilename());// .docx
                if (!allowedFileExtensions.contains(fileExtension)) {
                    actionResult.setErrorCode(ErrorCodeEnum.FILE_EXTENSION_NOT_ALLOWED);
                    return actionResult;
                }
                post.get().setHaveFiles(1);
                post.get().setFiles(cloudinaryService.uploadFile(updatePostDTO.getFiles()));
            }else {
                post.get().setHaveFiles(0);
                post.get().setFiles(null);
            }
            save(post.get());
            actionResult.setErrorCode(ErrorCodeEnum.OK);
            actionResult.setData(transform(post.get()));
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult deletePost(Integer postId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Post> post = postRepository.findById(postId);
            Optional<User> user = userRepository.findById(CurrentUserUtils.getCurrentUserId());
            if (post.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.POST_NOT_FOUND);
                return actionResult;
            }
            if (post.get().getUser().getUserId().equals(CurrentUserUtils.getCurrentUserId())) {
                postRepository.deleteById(postId);
                actionResult.setErrorCode(ErrorCodeEnum.OK);
            }
            else if (user.isPresent() && user.get().getRole().getName().equals("ADMIN")) {
                if(post.get().getPrivacyLevel() == PrivacyLevel.LOCKED){
                    actionResult.setErrorCode(ErrorCodeEnum.POST_IS_LOCKED);
                    return actionResult;
                }
                post.get().setIsActive(false);
                post.get().setPrivacyLevel(PrivacyLevel.LOCKED);
                save(post.get());
                actionResult.setErrorCode(ErrorCodeEnum.OK);
            }
            else {
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_PERMISSION);
            }

        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult unlockPost(Integer postId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.POST_NOT_FOUND);
                return actionResult;
            }
            post.get().setPrivacyLevel(PrivacyLevel.PRIVATE);
            save(post.get());
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }
    @Override
    public <S extends Post> S save(S entity) {
        return postRepository.save(entity);
    }

    @Override
    public Optional<Post> findById(Integer integer) {
        return postRepository.findById(integer);
    }

    @Override
    public boolean existsById(Integer integer) {
        return postRepository.existsById(integer);
    }

    @Override
    public long count() {
        return postRepository.count();
    }

    @Override
    public void deleteById(Integer integer) {
        postRepository.deleteById(integer);
    }


}
