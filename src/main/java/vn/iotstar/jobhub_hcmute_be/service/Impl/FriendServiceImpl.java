package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.constant.FriendStatus;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.GetFriendsModel;
import vn.iotstar.jobhub_hcmute_be.model.PageModel;
import vn.iotstar.jobhub_hcmute_be.repository.*;
import vn.iotstar.jobhub_hcmute_be.service.FriendService;
import vn.iotstar.jobhub_hcmute_be.utils.CurrentUserUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FriendServiceImpl implements FriendService {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    FriendUserRepository friendUserRepository;

    private GetFriendsModel transform (Friend friend, String currentUserId ) {
        User user = friend.getSender().getUserId().equals(currentUserId) ? friend.getReceiver() : friend.getSender();
        Optional<Student> student = studentRepository.findById(user.getUserId());
        Optional<Employer> employer = employerRepository.findById(user.getUserId());
        if (student.isPresent()) {
           return GetFriendsModel.transformStudent(student.get(),friend.getFriendId(),user.getUserId());
        } else if (employer.isPresent()) {
            return GetFriendsModel.transformEmployer(employer.get(),friend.getFriendId(),user.getUserId());
        }
        return null;
    }
    private ActionResult getFriendHandle (Integer page, Integer size , User user) {
        ActionResult actionResult = new ActionResult();
        try {
            Page<FriendUser> friends = friendUserRepository.findByUser(user, PageRequest.of(page-1, size));
            Map<String, Object> response = new HashMap<>();
            response.put("friendCount", user.getFriendCount());
            response.put("friends", PageModel.transform(friends));
            actionResult.setData(response);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        }
        catch (Exception e){
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }
    @Override
    public ActionResult getFriends(Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> currentUser = userRepository.findByUserId(CurrentUserUtils.getCurrentUserId());
            if (currentUser.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
            actionResult = getFriendHandle(page, size, currentUser.get());
        }
        catch (Exception e){
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

//    public ActionResult getFriendsPage(Integer page, Integer size) {
//        ActionResult actionResult = new ActionResult();
//        try {
//            Optional<User> currentUser = userRepository.findByUserId(CurrentUserUtils.getCurrentUserId());
//
//            Page<Friend> friends = friendRepository.findBySenderOrReceiverAndStatus(currentUser.get(), currentUser.get(), FriendStatus.ACCEPTED, PageRequest.of(page-1, size));
//            List<GetFriendsModel> responData =  friends.map(friend -> transform(friend,currentUser.get().getUserId())).getContent();
//            Map<String, Object> response = new HashMap<>();
//            response.put("friends", PageModel.transform(friends,responData));
//            response.put("friendCount", currentUser.get().getFriendCount());
//            actionResult.setData(response);
//            actionResult.setErrorCode(ErrorCodeEnum.OK);
//
//        }
//        catch (Exception e){
//            System.out.println(e);
//            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
//        }
//        return actionResult;
//    }
    @Override
    public ActionResult getFriendsUser(String userId, Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> user = userRepository.findByUserId(userId);
            if (user.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
            if(!user.get().getIsPublicFriend()){
                actionResult.setErrorCode(ErrorCodeEnum.NOT_PUBLIC_FRIEND);
                return actionResult;
            }
            actionResult = getFriendHandle(page, size, user.get());
        }
        catch (Exception e){
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }
//
//    public ActionResult getFriendsUserPage(String userId, Integer page, Integer size) {
//        ActionResult actionResult = new ActionResult();
//        try {
//            Optional<User> user = userRepository.findByUserId(userId);
//            if (user.isEmpty()) {
//                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
//                return actionResult;
//            }
//            if(!user.get().getIsPublicFriend()){
//                actionResult.setErrorCode(ErrorCodeEnum.NOT_PUBLIC_FRIEND);
//                return actionResult;
//            }
//            Page<Friend> friends = friendRepository.findBySenderOrReceiverAndStatus(user.get(), user.get(), FriendStatus.ACCEPTED, PageRequest.of(page-1, size));
//            List<GetFriendsModel> responData =  friends.map(friend -> transform(friend,user.get().getUserId())).getContent();
//            Map<String, Object> response = new HashMap<>();
//            response.put("friends", PageModel.transform(friends,responData));
//            response.put("friendCount", user.get().getFriendCount());
//            actionResult.setData(response);
//            actionResult.setErrorCode(ErrorCodeEnum.OK);
//
//        }
//        catch (Exception e){
//            System.out.println(e);
//            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
//        }
//        return actionResult;
//    }
    @Override
    public ActionResult getFriendRequest(Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> currentUser = userRepository.findByUserId(CurrentUserUtils.getCurrentUserId());

            if (currentUser.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
            Page<Friend> friends = friendRepository.findByReceiverAndStatus(currentUser.get(), FriendStatus.PENDING, PageRequest.of(page-1, size));
            List<GetFriendsModel> responData =  friends.map(friend -> transform(friend,currentUser.get().getUserId())).getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("friends", PageModel.transform(friends,responData));
            response.put("friendCount", currentUser.get().getFriendCount());
            actionResult.setData(response);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        }
        catch (Exception e){
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }
    @Override
    public ActionResult sendFriendRequest(String receiverId) {
        ActionResult actionResult = new ActionResult();
        try{
            Optional<User> sender = userRepository.findByUserId(CurrentUserUtils.getCurrentUserId());
            Optional<User> receiver = userRepository.findByUserId(receiverId);
            if(!sender.isPresent() || !receiver.isPresent()){
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
            Optional<Friend> friendOptional = friendRepository.findBySenderAndReceiver(sender.get(), receiver.get());
            if (!friendOptional.isPresent()) {
                Friend friend = new Friend();
                friend.setSender(sender.get());
                friend.setReceiver(receiver.get());
                friend.setStatus(FriendStatus.PENDING);
                friendRepository.save(friend);
                sender.get().setSenderCount(sender.get().getSenderCount() + 1);
                receiver.get().setReceiverCount(receiver.get().getReceiverCount() + 1);
                userRepository.save(sender.get());
                userRepository.save(receiver.get());
                actionResult.setErrorCode(ErrorCodeEnum.REQUEST_SENT_SUCCESSFULLY);
            }
            else {
                if(friendOptional.get().getStatus() == FriendStatus.ACCEPTED){
                    actionResult.setErrorCode(ErrorCodeEnum.FRIEND_EXISTED);
                }
                else if(friendOptional.get().getStatus() == FriendStatus.PENDING){
                    actionResult.setErrorCode(ErrorCodeEnum.REQUEST_ALREADY_SENT);
                }
            }
        }
        catch (Exception e){
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }

        return actionResult;
    }

    private FriendUser transformAccept ( User user, User friend) {
        FriendUser friendUser = new FriendUser();
        Optional<Student> student = studentRepository.findById(friend.getUserId());
        Optional<Employer> employer = employerRepository.findById(friend.getUserId());
        if (student.isPresent()) {
            friendUser.setFriendId(friend.getUserId());
            friendUser.setName(student.get().getFullName());
            friendUser.setAvatar(student.get().getAvatar());

        } else if  (employer.isPresent()) {
            friendUser.setFriendId(friend.getUserId());
            friendUser.setName(employer.get().getCompanyName());
            friendUser.setAvatar(employer.get().getLogo());
        }
        friendUser.setUser(user);
        return friendUser;
    }
    @Override
    public ActionResult acceptFriendRequest(String friendToAcceptId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> currentUser = userRepository.findByUserId(CurrentUserUtils.getCurrentUserId());
            Optional<User> friendToAccept = userRepository.findByUserId(friendToAcceptId);
            if (friendToAccept.isEmpty()|| currentUser.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
            Optional<Friend> friendRequestOptional = friendRepository.findBySenderAndReceiver(friendToAccept.get(), currentUser.get());
            if (friendRequestOptional.isPresent()) {
                FriendUser friendUser1 = transformAccept(friendToAccept.get(), currentUser.get());
                FriendUser friendUser0 = transformAccept(currentUser.get(), friendToAccept.get());
                currentUser.get().getFriends().add(friendUser0);
                friendToAccept.get().getFriends().add(friendUser1);
                currentUser.get().setFriendCount(currentUser.get().getFriendCount() + 1);
                friendToAccept.get().setFriendCount(friendToAccept.get().getFriendCount() + 1);
                currentUser.get().setReceiverCount(currentUser.get().getReceiverCount() - 1);
                friendToAccept.get().setSenderCount(friendToAccept.get().getSenderCount() - 1);
                userRepository.save(currentUser.get());
                userRepository.save(friendToAccept.get());
                friendRepository.delete(friendRequestOptional.get());
//                Friend friend = friendRequestOptional.get();
//                friend.setStatus(FriendStatus.ACCEPTED);
//                friendRepository.save(friend);
                actionResult.setErrorCode(ErrorCodeEnum.ACCEPT_FRIEND_REQUEST_SUCCESSFULLY);
            }
            else {
                actionResult.setErrorCode(ErrorCodeEnum.FRIEND_REQUEST_NOT_FOUND);
            }
        }
        catch (Exception e){
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;

    }
    @Override
    public ActionResult cancelSendFriendRequest(String senderId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> curentUser = userRepository.findByUserId(CurrentUserUtils.getCurrentUserId());
            Optional<User> sender = userRepository.findByUserId(senderId);
            if (curentUser.isEmpty() || sender.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
            Optional<Friend> friendOptional = friendRepository.findBySenderAndReceiver(sender.get(), curentUser.get());
            if (friendOptional.isPresent()) {
                if (friendOptional.get().getStatus() == FriendStatus.PENDING) {
                    friendRepository.delete(friendOptional.get());
                    sender.get().setSenderCount(sender.get().getSenderCount() - 1);
                    curentUser.get().setReceiverCount(curentUser.get().getReceiverCount() - 1);
                    userRepository.save(sender.get());
                    userRepository.save(curentUser.get());
                    actionResult.setErrorCode(ErrorCodeEnum.CANCLE_FRIEND_REQUEST_SUCCESSFULLY);
                }
                else {
                    actionResult.setErrorCode(ErrorCodeEnum.FRIEND_EXISTED);
                }
            }
            else {
                actionResult.setErrorCode(ErrorCodeEnum.FRIEND_REQUEST_NOT_FOUND);
            }
        }
        catch (Exception e){
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult deleteFriend(String friendId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> currentUser = userRepository.findByUserId(CurrentUserUtils.getCurrentUserId());
            Optional<User> friend = userRepository.findByUserId(friendId);
            if (friend.isEmpty() || currentUser.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
//            Optional<Friend> friendOptional = friendRepository.findBySenderAndReceiver(currentUser.get(), friend.get());
//            Optional<Friend> friendOptional1 = friendRepository.findBySenderAndReceiver(friend.get(), currentUser.get());
//            if (friendOptional.isPresent()) {
//              friendRepository.delete(friendOptional.get());
//            }
//            if (friendOptional1.isPresent()) {
//              friendRepository.delete(friendOptional1.get());
//            }
            User curent = currentUser.get();
            Optional<FriendUser> friendUser = friendUserRepository.findByUserAndFriendId(curent, friendId);
            if (friendUser.isPresent()) {
                curent.getFriends().remove(friendUser.get());
                curent.setFriendCount(curent.getFriendCount() - 1);
                userRepository.save(curent);
                actionResult.setErrorCode(ErrorCodeEnum.DELETE_FRIEND_SUCCESSFULLY);
            }
            else {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FRIEND);
            }

        }
        catch (Exception e){
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    public <S extends Friend> S save(S entity) {
        return friendRepository.save(entity);
    }

    public Optional<Friend> findById(Integer integer) {
        return friendRepository.findById(integer);
    }

    public boolean existsById(Integer integer) {
        return friendRepository.existsById(integer);
    }


}
