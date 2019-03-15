package edu.buffalo.cse.cse486586.groupmessenger2;

public class Message{
    float msg_id;
    String message;
    int sender;
    int deliverable;
    boolean send = false;

    public Message(float id, String msg, int sender){
        this.msg_id = id;
        this.message = msg;
        this.sender = sender;
        this.deliverable = 0;
    }

    public Message(float id, String msg, int sender, int deliverable){
        this.msg_id = id;
        this.message = msg;
        this.sender = sender;
        this.deliverable = deliverable;
    }

    public static java.util.Comparator<Message> id = new java.util.Comparator<Message>(){
        public int compare(Message m1, Message m2){
            float m1_id = m1.getMsg_id();
            float m2_id = m2.getMsg_id();

            return Float.compare(m1_id, m2_id);
        }
    };

    public void setSend(boolean b){
        this.send = b;
    }

    public boolean getSend(){
        return this.send;
    }

    public float getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(float msg_id) {
        this.msg_id = msg_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public void setDeliverable(int d){
        this.deliverable = d;
    }

    public int getDeliverable() {
        return deliverable;
    }
}