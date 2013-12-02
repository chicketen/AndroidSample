package chicketen.httppost;

public interface HttpPostListener {
    abstract public void postCompletion(byte[] response);
    abstract public void postFialure();
}
