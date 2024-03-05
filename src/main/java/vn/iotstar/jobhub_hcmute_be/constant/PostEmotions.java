package vn.iotstar.jobhub_hcmute_be.constant;

public enum PostEmotions {
    LIKE("LIKE"), LOVE("LOVE"), HAHA("HAHA"), WOW("WOW"), SAD("SAD"), ANGRY("ANGRY");
    String name;

    PostEmotions(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public static boolean checkEmotion(PostEmotions emotions, PostEmotions emotionsCheck) {
        if (emotions != null) {
            return emotions.equals(emotionsCheck);
        }
        return false;
    }
}
