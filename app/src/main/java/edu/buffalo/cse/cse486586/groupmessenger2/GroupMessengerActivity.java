package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import static android.content.ContentValues.TAG;


/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    static String P_TAG="PartiksTag";
    static String P_TAG2="LongTag";
    //static String[] remotePorts = {"11108","11112","11116","11120","11124"};
    static ArrayList<String> remotePorts = new ArrayList<String>();

    public static void setRemotePorts(ArrayList<String> remotePorts) {
        GroupMessengerActivity.remotePorts = remotePorts;
    }

    public static ArrayList<String> getRemotePorts() {
        return remotePorts;
    }

    static final int SERVER_PORT = 10000;
    private static int myIndex=0;
    static final int timeout = 5000;
    static String failed_avd="";
    ArrayList<Message> msgs = new ArrayList<Message>();

    String myPort=null;
    String portStr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
        ArrayList<String> lul = new ArrayList<String>();
        lul.add("11108");
        lul.add("11112");
        lul.add("11116");
        lul.add("11120");
        lul.add("11124");
        setRemotePorts(lul);

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver(), "test"));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        //partiks code start
        final EditText editText = (EditText) findViewById(R.id.edit_text);
        findViewById(R.id.button4).setOnClickListener( new View.OnClickListener(){
            public void onClick(View v){
                String msg = editText.getText().toString();
                editText.setText("");
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg);
            }
        });

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        for (int i=0;i<5;i++){
            if(remotePorts.get(i).equals(myPort)){
                this.myIndex=(i+1);
                Log.e(P_TAG,"SERVER MYINDEX = "+ this.myIndex);
                break;
            }
        }
        try {
            Log.e(P_TAG, "SERVER: TRYING TO CREATE SERVER SOCKET - " + SERVER_PORT + " " + myPort + " MYINDEX = " + myIndex);
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {

            Log.e(TAG, "Can't create a ServerSocket");
            e.printStackTrace();
            return;
        }


        //partiks code end
    }

    //partiks added ServerTask and ClientTask classes from PA-1 code

    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        private int running_id=0;
        private int max_group_id=0;

        @Override
        protected Void doInBackground(ServerSocket... sockets) {

            ServerSocket serverSocket = sockets[0];

            Socket socket = null;
            int found = 0;

            String sender = "";
            ListIterator<Message> itr;

            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
            //partiks code start
            //Parth Patel. UB Person name/number: parthras/50290764
            //reference for Java Socket API code: https://www.geeksforgeeks.org/socket-programming-in-java/
            //reference for improved Java Socket API code: https://www.baeldung.com/a-guide-to-java-sockets
            //if (myIndex != 5) {
                while (true) {
                    try {
                        socket = serverSocket.accept();
                        /* if(remotePorts.size() == 4 && found == 0){
                            String arr[]= new String[]{"11108","11112","11116","11120","11124"};
                            for(int z=0;z<5;z++){
                                if(!remotePorts.contains(arr[z])){
                                    failed_avd = arr[z];
                                    Log.e(P_TAG, "SERVER FOUND CRASHED INSTANCE = " + failed_avd);
                                    found = 1;
                                }
                            }
                        } */

                        //Log.e(P_TAG, "Server Socket Timeout Value: " + socket.getSoTimeout());
                        Log.e(P_TAG, "SERVER CONNECTED !!!  REMOTEPORT SIZE = " + remotePorts.size());
                        //New Approach
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String temp;
                        sender = in.readLine();
                        while ((temp = in.readLine()) != null) {
                            Log.e(P_TAG, "SERVER READ A LINE:  = "+temp);
                            if ("AAI_GAYU".equals(temp)) {
                                break;
                            }


                            /*
                            else if("FAILED_AVD".equals(temp)){
                                String failed_avd = in.readLine();
                                Log.e(P_TAG, "<<<<<<<<<<<< OUTSIDE IF SERVER DETECTED " + failed_avd  + " from remorePorts = " + getRemotePorts() + " remotePort size = " + remotePorts.size());
                                if(remotePorts.contains(failed_avd)){
                                    remotePorts.remove(failed_avd);
                                    Log.e(P_TAG, "<<<<<<<<<<<<<< INSIDE IF REMOVED " + failed_avd  + "from remorePorts = " + getRemotePorts() + " remotePort size = " + remotePorts.size());
                                }
                                itr = msgs.listIterator();
                                //marking all messages from failed_avd as deliverable so that they dont mess up with other messages' delivery
                                Log.e(P_TAG2, "-------RESETTING ALL FAILED_AVD MESSAGES TO DELIVERABLE ");
                                while (itr.hasNext()) {
                                    Message m2 = itr.next();
                                    Log.e(P_TAG2, "<<<<<<<<<<<<<<<<<<< Comparing m2.getSender = "+ m2.getSender() + " to failed_avd" +failed_avd );
                                    if(m2.getSender().equals(failed_avd)){
                                        int m_index = msgs.indexOf(m2);
                                        Log.e(P_TAG2, "<<<<<<<<<<<<<<<< Index of failed_avd msg = " + m_index + " msg_id = " + m2.getMsg_id() + " msg: " + m2.getMessage());
                                        msgs.get(m_index).setDeliverable(1);
                                    }
                                }
                                // now again rechecking for deliverable messages and delivering them

                                int msgsToSend = 0;
                                for (int i = 0; i < msgs.size(); i++) {
                                    if (msgs.get(i).getDeliverable() == 1) {
                                        msgsToSend++;
                                    } else {
                                        break;
                                    }
                                }
                                Log.e(P_TAG, "SEVER: FAILED_AVD NOTIFICATION MSGSTOSEND CALCULATION = " + msgsToSend);
                                String msgsToDeliver[] = new String[2];
                                for (int i = 0; i < msgs.size() && i < msgsToSend; i++) {
                                    if (msgs.get(i).getSend() == true) {
                                        Log.e(P_TAG2, "Ignoring" + msgs.get(i).getMsg_id() + " " + msgs.get(i).getMessage());
                                        continue;
                                    } else if (msgs.get(i).getSend() == false && i < msgsToSend) {
                                        msgsToDeliver[0] = Float.toString(msgs.get(i).getMsg_id());
                                        msgsToDeliver[1] = msgs.get(i).getMessage();
                                        msgs.get(i).setSend(true);
                                        Log.e(P_TAG2, "<<<<<<<<<<< PUBLISHING " + msgsToDeliver[0] + " " + msgsToDeliver[1]);
                                        new OnPTestClickListener((TextView) findViewById(R.id.textView1), msgsToDeliver[0], msgsToDeliver[1], getContentResolver(), "group");
                                        publishProgress(msgsToDeliver);
                                    }
                                }

                            }
                            */
                            else if ("ID AAPO LA".equals(temp)) {
                                //ID finding logic here
                                int max = max_group_id;
                                if (running_id > max_group_id) {
                                    max = running_id;
                                }
                                running_id++;
                                max += 1;
                                //sender = in.readLine();
                                //String send = in.readLine();
                                //sender = Integer.parseInt(send);
                                temp = in.readLine();
                                Log.e(P_TAG, "SERVER: FINDING ID ON SERVER SIDE: " + running_id + " for msg: " + temp + " from sender: " + sender);

                                out.println("MARO PROPOSED ID: ");
                                out.println(max);
                                msgs.add(new Message(Float.parseFloat(((String) Integer.toString(running_id)) + "." + myIndex), temp, sender));
                                Collections.sort(msgs, Message.id);
                            }

                            else if ("NAVO MSG".equals(temp)) {
                                //Log.e(P_TAG, "NEW EXPERIMENT: MAX_GROUP_ID = " + max_group_id);
                                String[] msg_obj = new String[2];
                                //String send = in.readLine();
                                msg_obj[0] = in.readLine();
                                msg_obj[1] = in.readLine();
                                if(sender.equals(failed_avd) || msg_obj[0] == null || msg_obj[1] == null){
                                    Log.e(P_TAG, "SERVER FOUND CRASHED AVD IN CONFIRMED ID SECTION --------------------->>>>>>>>>> " + sender);
                                    remotePorts.remove(sender);
                                    failed_avd=sender;
                                    itr = msgs.listIterator();
                                    Log.e(P_TAG2, "------------------------------------------------------ SENDER WAS THE FAILED_AVD");
                                    while (itr.hasNext()) {
                                        Message m2 = itr.next();
                                        if(m2.getSender().equals(failed_avd)){
                                            int m_index = msgs.indexOf(m2);
                                            Message m = new Message(m2.getMsg_id(), m2.getMessage(), failed_avd, 1);
                                            msgs.set(m_index, m);
                                        }
                                    }
                                }
                                else{

                                    if (remotePorts.contains(sender) ) {
                                        Log.e(P_TAG, "REMOTE PORTS HAS THE SENDER: " + sender);

                                        if (max_group_id < Float.parseFloat(msg_obj[0])) {
                                            max_group_id = (int) (Float.parseFloat(msg_obj[0]) * 100) / 100;
                                            //Log.e(P_TAG, "NEW EXPERIMENT: MAX_GROUP_ID = " + max_group_id);
                                        }
                                        Log.e(P_TAG, "SERVER: MSG_OBJECT CREATED ID: " + msg_obj[0] + " MSG: " + msg_obj[1]);
                                        int m_index = -1;

                                        /*ListIterator<Message> iterator = msgs.listIterator();

                                        Log.e(P_TAG2, "------------------------------------------------------");
                                        while (iterator.hasNext()) {
                                            Message m2 = iterator.next();
                                            Log.e(P_TAG2, "MSG ID:          " + m2.getMsg_id());
                                            Log.e(P_TAG2, "MSG CONTENT:     " + m2.getMessage());
                                            Log.e(P_TAG2, "MSG Deliverable: " + m2.getDeliverable());
                                            Log.e(P_TAG2, "MSG Sender:      " + m2.getSender());
                                            Log.e(P_TAG2, "MSG SEND:        " + m2.getSend());
                                            Log.e(P_TAG2, "<>");
                                        }
                                        Log.e(P_TAG2, "------------------------------------------------------"); */

                                        Log.e(P_TAG, "Server-1 error: Tried to find msg: " + msg_obj[1]);
                                        for (Message m : msgs) {
                                            if (m.getMessage().equals(msg_obj[1])) {
                                                m_index = msgs.indexOf(m);
                                            }
                                        }
                                        //if(m_index == -1){
                                            //msgs.add(new Message(Float.parseFloat(msg_obj[0]), msg_obj[1],send, 1));
                                            //Collections.sort(msgs, Message.id);
                                        //}else{
                                            Message m = new Message(Float.parseFloat(msg_obj[0]), msg_obj[1], msgs.get(m_index).getSender(), 1);
                                            msgs.get(m_index).setMsg_id(Float.parseFloat(msg_obj[0]));
                                            msgs.get(m_index).setDeliverable(1);
                                        //}



                                        Collections.sort(msgs, Message.id);

                                        itr = msgs.listIterator();

                                        Log.e(P_TAG2, "------------------------------------------------------");
                                        while (itr.hasNext()) {
                                            Message m2 = itr.next();
                                            Log.e(P_TAG2, "MSG ID:          " + m2.getMsg_id());
                                            Log.e(P_TAG2, "MSG CONTENT:     " + m2.getMessage());
                                            Log.e(P_TAG2, "MSG Deliverable: " + m2.getDeliverable());
                                            Log.e(P_TAG2, "MSG Sender:      " + m2.getSender());
                                            Log.e(P_TAG2, "MSG SEND:        " + m2.getSend());
                                            Log.e(P_TAG2, "<>");
                                        }
                                        Log.e(P_TAG2, "------------------------------------------------------");
                                        int msgsToSend = 0;
                                        for (int i = 0; i < msgs.size(); i++) {
                                            if (msgs.get(i).getDeliverable() == 1) {
                                                msgsToSend++;
                                            } else {
                                                break;
                                            }
                                        }
                                        Log.e(P_TAG, "SERVER: MSGSTOSEND CALCULATION = " + msgsToSend);
                                        String msgsToDeliver[] = new String[2];
                                        for (int i = 0; i < msgs.size() && i < msgsToSend; i++) {
                                            if (msgs.get(i).getSend() == true) {
                                                Log.e(P_TAG2, "Ignoring" + msgs.get(i).getMsg_id() + " " + msgs.get(i).getMessage());
                                                continue;
                                            } else if (msgs.get(i).getSend() == false && msgs.get(i).getDeliverable() == 1) {
                                                msgsToDeliver[0] = Float.toString(msgs.get(i).getMsg_id());
                                                msgsToDeliver[1] = msgs.get(i).getMessage();
                                                msgs.get(i).setSend(true);
                                                Log.e(P_TAG2, "<<<<<<<<<<< PUBLISHING " + msgsToDeliver[0] + " " + msgsToDeliver[1]);
                                                new OnPTestClickListener((TextView) findViewById(R.id.textView1), msgsToDeliver[0], msgsToDeliver[1], getContentResolver(), "group");
                                                publishProgress(msgsToDeliver);
                                            }
                                        }
                                    }
                                }

                                //publishProgress(msg_obj);

                            } else {
                                Log.e(P_TAG, "WEIRD SERVER ENTERED LAST ELSE with msg: " + temp);
                            }
                        }
                        out.println("SERVER_AAI_GAYU");
                        in.close();
                        out.close();
                        socket.close();

                    } catch (SocketTimeoutException soe) {
                        Log.e(P_TAG, "SERVER SIDE SOCKET TIMEOUT ------------------ HUEHUEHUEHUEHUE " + sender);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

        //}
        //return null;

        }

        protected boolean checkForFailure(String str, String sender){
            if(str == null || str.equals("")){
                Log.e(P_TAG, "----------------------------Server "+ myPort+" checkForFailure detected crash of server: " + sender);
                return false;
            }
            return true;
        }

        protected void onProgressUpdate(String...strings) {
            //Log.e(P_TAG, "SERVER: PROGRESS UPDATE BEHAVIOR >>>>>>>>>>>>> WITH ID: " + " " + strings[0] + " AND MSG:  " + strings[1]);
            //String idReceived = strings[0].trim();
            String strReceived = strings[1].trim();

            TextView localTextView = (TextView) findViewById(R.id.textView1);
            localTextView.append(strReceived + "\n");
            return;
        }
    }


    /***
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     *
     * @author stevko
     *
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        int i = 0;
        ListIterator<Message> c_itr;
                //id_found_flag=0;
        float proposals[] = new float[]{0, 0, 0, 0, 0};
        int end_flag  = 1;

        @Override
        protected Void doInBackground(String... c_msgs) {
            // reference: http://java.candidjava.com/tutorial/find-the-index-of-the-largest-number-in-an-array.htm
            //
            //if(myIndex != 5) {

                try {

                    for (i = 0; i < remotePorts.size(); i++) {

                        Log.e(P_TAG, "CLIENT ATTEMPTING TO CONNECT TO " + remotePorts.get(i) + " REMOTEPORT SIZE = " + remotePorts.size());
                        socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(remotePorts.get(i)));
                        //socket.setSoTimeout(timeout);
                        out = new PrintWriter(socket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        //id_found_flag=0;
                        int flag = 1;
                        //when this flag is 1 it means server is alive and communicating, when 0, that means this instance of loop contains the crashed avd id

                        out.println(myPort);
                        out.println("ID AAPO LA");
                        out.println(c_msgs[0]);
                        out.println("AAI_GAYU");
                        String temp = in.readLine();

                        if(temp == null && remotePorts.size() == 5){

                            Log.e(P_TAG,"-----------------------------------PARTIKS WON OVER SOCKETS !!!! crashed server = " + remotePorts.get(i) + " for msg = " + c_msgs[0]);
                            // multicasting that this server has failed so that server deletes the messages received from this server
                                failed_avd = remotePorts.get(i);
                                remotePorts.remove(i);
                            //trying new logic: sorting the msgs list from the client first, then sending others.

                            for(int j=0;j<msgs.size();j++){
                                if(msgs.get(j).getSender().equals(failed_avd)){
                                    msgs.get(j).setDeliverable(1);
                                }
                            }

                            i-=1;
                            flag = 0;
                            end_flag = 0;
                            //throw new SocketTimeoutException();
                        }else{
                            //Log.e(P_TAG,"PARTIKS DIDNT WIN OVER SOCKETS BUT STILL DRAGGED THIS SHIT OUT OF READLINE:" + temp + " from server ");
                        }


                        while ((temp != null) || flag == 1 ) {
                            //Log.e(P_TAG, "TESTING INPUT STREAMS = " + temp + " from server " + remotePorts.get(i) + " while waiting to get ID for client side msg " + msgs[0]);
                            if ("MARO PROPOSED ID: ".equals(temp)) {
                                temp = in.readLine();
                                String id_gen = temp + "." + (i + 1);
                                id_gen += myIndex;
                                proposals[i] = Float.parseFloat(id_gen);
                                Log.e(P_TAG, "CLIENT: GOT ID " + temp + " FROM SERVER: " +  remotePorts.get(i) + " " + id_gen + " msg: " + c_msgs[0] + " float = " + proposals[i] + " \n\n");
                            }
                            if ("SERVER_AAI_GAYU".equals(temp)) {
                                break;
                            }
                            temp = in.readLine();
                        }
                        out.flush();
                        //partiks code end
                        out.close();
                        in.close();
                        socket.close();
                    }


                    //-------------------------- ALL PROPOSALS ARE COLLECTED ---------------------------------------------------------------------
                    float max = proposals[0];

                    for (int j = 0; j < proposals.length; j++) {
                        if (max < proposals[j]) {
                            max = proposals[j];
                        }
                    }
                    //id_found_flag = 1;
                    Log.e(P_TAG, "CLIENT: PICKING UP ID VALUE = " + max + " for msg: " + c_msgs[0] + " IMP <<<<<<<<<<<<<<<<<<<<<<<\n\n");
                    // we got ID till now, time to sort the message list of sender first, then send the message to other processes (AVDs)
                    //msgs.add( new Message(max, c_msgs[0], myPort, 1));
                    for(int j=0;j<msgs.size();j++){
                        if(msgs.get(j).getMessage().equals(c_msgs[0])){
                            msgs.get(j).setMsg_id(max);
                            msgs.get(j).setDeliverable(1);
                            break;
                        }
                    }
                    Log.e(P_TAG2, "CLIENT ------------------------------------------------------");
                    for(int j=0; j<msgs.size(); j++){
                        Log.e(P_TAG2, "MSG ID:          " + msgs.get(j).getMsg_id());
                        Log.e(P_TAG2, "MSG CONTENT:     " + msgs.get(j).getMessage());
                        Log.e(P_TAG2, "MSG Deliverable: " + msgs.get(j).getDeliverable());
                        Log.e(P_TAG2, "MSG Sender:      " + msgs.get(j).getSender());
                        Log.e(P_TAG2, "MSG SEND:        " + msgs.get(j).getSend());
                        Log.e(P_TAG2, "<>");
                    }
                    Log.e(P_TAG2, "CLIENT ------------------------------------------------------");
                    Collections.sort(msgs, Message.id);
                    /*
                    Log.e(P_TAG2, "CLIENT ------------------------------------------------------");
                    for(int j=0; j<msgs.size(); j++){
                        Log.e(P_TAG2, "MSG ID:          " + msgs.get(j).getMsg_id());
                        Log.e(P_TAG2, "MSG CONTENT:     " + msgs.get(j).getMessage());
                        Log.e(P_TAG2, "MSG Deliverable: " + msgs.get(j).getDeliverable());
                        Log.e(P_TAG2, "MSG Sender:      " + msgs.get(j).getSender());
                        Log.e(P_TAG2, "MSG SEND:        " + msgs.get(j).getSend());
                        Log.e(P_TAG2, "<>");
                    }
                    Log.e(P_TAG2, "CLIENT ------------------------------------------------------");
                    int msgsToSend = 0;
                    for (int j = 0; j < msgs.size(); j++) {
                        if (msgs.get(j).getDeliverable() == 1) {
                            msgsToSend++;
                        } else {
                            break;
                        }
                    }
                    Log.e(P_TAG, "SERVER: MSGSTOSEND CALCULATION = " + msgsToSend);
                    String msgsToDeliver[] = new String[2];
                    for (int j = 0; j < msgs.size() && j < msgsToSend; j++) {
                        if (msgs.get(j).getSend() == true) {
                            Log.e(P_TAG2, "Ignoring" + msgs.get(j).getMsg_id() + " " + msgs.get(j).getMessage());
                            continue;
                        } else if (msgs.get(j).getSend() == false && msgs.get(j).getDeliverable() == 1) {
                            msgsToDeliver[0] = Float.toString(msgs.get(j).getMsg_id());
                            msgsToDeliver[1] = msgs.get(j).getMessage();
                            msgs.get(j).setSend(true);
                            Log.e(P_TAG2, "<<<<<<<<<<< PUBLISHING FROM CLIENT:"+ myPort+ " ID: " + msgsToDeliver[0] + " " + msgsToDeliver[1]);
                            new OnPTestClickListener((TextView) findViewById(R.id.textView1), msgsToDeliver[0], msgsToDeliver[1], getContentResolver(), "group");
                        }
                    } */


                    // ---------------------------------------------------------- ACTUAL MULTICASTING OF CONFIRMED ID MESSAGE ----------------------------------------------------------
                    for (i = 0; i < remotePorts.size(); i++) {
                        /*
                        if(remotePorts.get(i).equals(myPort)){
                            continue;
                        } */
                        Log.e(P_TAG, "CLIENT ATTEMPTING TO CONNECT TO " + remotePorts.get(i) + " REMOTEPORT SIZE = " + remotePorts.size() + " for sending client side msg " + c_msgs[0]);
                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(remotePorts.get(i)));

                        //socket.connect(new InetSocketAddress(10.0.2.2, Integer.parseInt(remotePorts[i])), 1000);

                        String msgToSend = c_msgs[0];
                        /*
                         * TODO: Fill in your client code that sends out a message.
                         */
                        //partiks code start
                        if (socket.isConnected() && (!socket.isClosed())) {
                        } else {
                            Log.e(P_TAG, "OLD WIZARD TRICK WORKED ????");
                            return null;
                        }
                        //new approach
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        //broadcasting message with its respective ID
                        //if (out.checkError()) {
                            //Log.e(P_TAG, "TESTING OUTPUT STREAMS NOT ABLE TO WRITE TO " + remotePorts.get(i) + " is ready for sending client side msg: " + msgToSend);
                        //}
                        out.println(myPort);
                        out.println("NAVO MSG");
                        out.println(max);
                        out.println(msgToSend);
                        out.println("AAI_GAYU");
                        String temp;
//                        if (in.ready()) {
//                            Log.e(P_TAG, "Client Says server" + remotePorts.get(i) + " is ready for sending client side msg: " + msgToSend);
                            while ((temp = in.readLine()) != null) {
                                if ("SERVER_AAI_GAYU".equals(temp)) {
                                    Log.e(P_TAG, "CLIENT SUCCESSFULLY SENT MSG TO " + remotePorts.get(i) + " REMOTEPORT SIZE = " + remotePorts.size() + " for sending client side msg " + c_msgs[0] + " loop iteration " + i);
                                    break;
                                }
                            }
//                        } else {
//                            Log.e(P_TAG, "CLIENT SAYS SERVER NOT READY ! in.read() works " + remotePorts.get(i)+ " while sending client side msg: " + msgToSend);
                        //}
                        out.flush();
                        //partiks code end
                        out.close();
                        in.close();
                        socket.close();
                        Thread.sleep(500);
                    }

                    if(end_flag == 0){
                        // multicasting that this server has failed so that server deletes the messages received from this server
                        /*

                                Log.e(P_TAG, "IN THE ENDGAME NOW, ENDFLAG HAS BEEN SET - " + remotePorts.size() + " failed_avd: " + failed_avd);
                                for (int j = 0; j < remotePorts.size(); j++) {
                                    Log.e(P_TAG, "Multicasting to" + remotePorts.get(j) + " that this server"+ failed_avd +" has failed");
                                    Socket socket2 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(remotePorts.get(j)));
                                    PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);
                                    BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
                                    out2.println("FAILED_AVD");
                                    out2.println(failed_avd);
                                    out2.println("AAI_GAYU");
                                    String temp2 = in2.readLine();
                                    while (temp2 != null) {
                                        if ("SERVER_AAI_GAYU".equals(temp2)) {
                                            break;
                                        }
                                        temp2 = in2.readLine();
                                    }
                                    out2.flush();
                                    out2.close();
                                    in2.close();
                                    socket2.close();
                                    Thread.sleep(500);
                                }   */

                    }

                } catch (UnknownHostException e) {
                    Log.e(TAG, "ClientTask UnknownHostException");
                    e.printStackTrace();

                } catch (SocketTimeoutException ste) {
                    //out.flush();
                    out.flush();
                    out.close();
                    //Log.e(P_TAG, "CLIENT SOCKET TIMEOUT HUEHUEHUEHUE ----------------" + remotePorts.get(i));
                    //remotePorts.remove(i);

                    ste.printStackTrace();
                } catch (IOException e) {
                    Log.e(P_TAG, "WHY IT COME HERE THOUGH ????");
                    Log.e(TAG, "ClientTask socket IOException");
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e(P_TAG, "CATCHED THE MOST GENERIC EXCEPTION");
                    e.printStackTrace();
                }
                finally {
                    //if(id_found_flag == 0){
                        //doInBackground(msgs);
                    //}
                    out.flush();
                    //partiks code end
                    out.close();
                    try {
                        in.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            //}
            return null;
        }
    }


    //partiks appended code end



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
}
