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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    static String[] remotePorts = {"11108","11112","11116","11120","11124"};
    static final int SERVER_PORT = 10000;
    private static int myIndex=0;

    String myPort=null;
    String portStr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

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
            if(remotePorts[i].equals(myPort)){
                this.myIndex=(i+1);
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

        ArrayList<Message> msgs = new ArrayList<Message>();
        private int running_id=0;
        private int max_group_id=0;

        @Override
        protected Void doInBackground(ServerSocket... sockets) {

            ServerSocket serverSocket = sockets[0];

            Socket socket = null;

            String msgReceived="",msg=null;

            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
            //partiks code start
            //Parth Patel. UB Person name/number: parthras/50290764
            //reference for Java Socket API code: https://www.geeksforgeeks.org/socket-programming-in-java/
            //reference for imporoved Java Socket API code: https://www.baeldung.com/a-guide-to-java-sockets

            while(true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    //New Approach
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String temp;
                    while ((temp = in.readLine()) != null) {
                        if ("AAI_GAYU".equals(temp)) {
                            break;
                        }
                        else if ("ID AAPO LA".equals(temp)) {
                            //ID finding logic here
                            int max = max_group_id;
                            if(running_id > max_group_id){
                                max = running_id;
                            }
                            running_id++;
                            max+=1;
                            temp = in.readLine();
                            String sender = in.readLine();
                            Log.e(P_TAG, "SERVER: FINDING ID ON SERVER SIDE: " + running_id + " for msg: " + temp + " from sender: " + sender);

                            out.println("MARO PROPOSED ID: ");
                            out.println(max);
                            msgs.add( new Message( Float.parseFloat( ((String) Integer.toString(running_id)) + "."+myIndex ), temp, Integer.parseInt(sender) ));
                            Collections.sort(msgs, Message.id);
                        }
                        else if ("NAVO MSG".equals(temp)) {
                            Log.e(P_TAG, "NEW EXPERIMENT: MAX_GROUP_ID = "+max_group_id);
                            String[] msg_obj = new String[2];
                            msg_obj[0] = in.readLine();
                            msg_obj[1] = in.readLine();
                            if(max_group_id < Float.parseFloat(msg_obj[0])){
                                max_group_id = (int) (Float.parseFloat(msg_obj[0])*100)/100;
                                Log.e(P_TAG, "NEW EXPERIMENT: MAX_GROUP_ID = "+max_group_id);
                            }
                            Log.e(P_TAG, "SERVER: MSG_OBJECT CREATED ID: " + msg_obj[0] + " MSG: " + msg_obj[1]);
                            int m_index = -1;
                            for(Message m: msgs){
                                if(m.getMessage() != null && m.getMessage().equals(msg_obj[1])){
                                    m_index = msgs.indexOf(m);
                                }
                            }
                            Message m = new Message(Float.parseFloat(msg_obj[0]), msg_obj[1], msgs.get(m_index).getSender(), 1);
                            msgs.set(m_index, m);
                            ListIterator<Message> iterator = msgs.listIterator();
                            /*
                            Log.e(P_TAG2,"------------------------------------------------------");
                            while(iterator.hasNext()){
                                Message m2 = iterator.next();
                                Log.e(P_TAG2, "MSG ID: " + m2.getMsg_id());
                                Log.e(P_TAG2, "MSG CONTENT: " + m2.getMessage());
                                Log.e(P_TAG2, "MSG Deliverable: " + m2.getDeliverable());
                                Log.e(P_TAG2, "MSG SEND: " + m2.getSend());
                            }
                            Log.e(P_TAG2,"------------------------------------------------------"); */
                            Collections.sort(msgs, Message.id);
                            //Message m = new Message(Float.parseFloat(msg_obj[0]), msg_obj[1], msgs.get(m_index).getSender(), 1);
                            //msgs.indexOf();
                            iterator = msgs.listIterator();
                            /*
                            Log.e(P_TAG2,"------------------------------------------------------");
                            while(iterator.hasNext()){
                                Message m2 = iterator.next();
                                Log.e(P_TAG2, "MSG ID: " + m2.getMsg_id());
                                Log.e(P_TAG2, "MSG CONTENT: " + m2.getMessage());
                                Log.e(P_TAG2, "MSG Deliverable: " + m2.getDeliverable());
                                Log.e(P_TAG2, "MSG SEND: " + m2.getSend());
                            }
                            Log.e(P_TAG2,"------------------------------------------------------"); */
                            int msgsToSend=0;
                            for(int i=0; i< msgs.size();i++){
                                if(msgs.get(i).getDeliverable() == 1){
                                    msgsToSend++;
                                }else {
                                    break;
                                }
                            }
                            Log.e(P_TAG, "SERVER: MSGSTOSEND CALCULATION = " + msgsToSend);
                            String msgsToDeliver[] = new String[2];
                            for(int i=0; i<msgs.size() && i<msgsToSend; i++){
                                if(msgs.get(i).getSend() == true){
                                    Log.e(P_TAG2, "Ignoring" + msgs.get(i).getMsg_id() + " " + msgs.get(i).getMessage() );
                                    continue;
                                }else if(msgs.get(i).getSend() == false && i<msgsToSend){
                                    msgsToDeliver[0]=Float.toString(msgs.get(i).getMsg_id());
                                    msgsToDeliver[1]=msgs.get(i).getMessage();
                                    msgs.get(i).setSend(true);
                                    Log.e(P_TAG2, "<<<<<<<<<<< PUBLISHING " + msgsToDeliver[0] + " " + msgsToDeliver[1]);
                                    new OnPTestClickListener((TextView) findViewById(R.id.textView1), msgsToDeliver[0], msgsToDeliver[1], getContentResolver(), "group");
                                    publishProgress(msgsToDeliver);
                                }
                            }
                            //publishProgress(msg_obj);

                        }
                        else {
                            msgReceived = temp;
                            //publishProgress(msgReceived);
                        }
                    }
                    out.println("SERVER_AAI_GAYU");
                    in.close();
                    out.close();
                    socket.close();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        protected void onProgressUpdate(String...strings) {
            Log.e(P_TAG, "SERVER: PROGRESS UPDATE BEHAVIOR >>>>>>>>>>>>> WITH ID: " + " " + strings[0] + " AND MSG:  " + strings[1]);
            String idReceived = strings[0].trim();
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

        @Override
        protected Void doInBackground(String... msgs) {
            // reference: http://java.candidjava.com/tutorial/find-the-index-of-the-largest-number-in-an-array.htm
            //
            int test_avds = 5;
            float proposals[]= new float[]{0,0,0,0,0};
            try {

                for(int i=0; i<test_avds;i++) {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(remotePorts[i]));
                    //socket.setSoTimeout(5*1000);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.println("ID AAPO LA");
                    out.println(msgs[0]);
                    out.println(myIndex);
                    out.println("AAI_GAYU");
                    String temp;
                    while((temp = in.readLine())!=null){
                        if("MARO PROPOSED ID: ".equals(temp)){
                            temp = in.readLine();
                            String id_gen = temp+"."+(i+1);
                            id_gen += myIndex;
                            proposals[i] = Float.parseFloat(id_gen);
                            //Log.e(P_TAG, "CLIENT: GOT ID " + temp + " FROM SERVER: " + remotePorts[i] + " "+ id_gen + " msg: "+ msgs[0] + " float = " + proposals[i]+" \n\n");
                        }
                        if("SERVER_AAI_GAYU".equals(temp)){
                            break;
                        }
                    }
                    out.flush();
                    //partiks code end
                    out.close();
                    in.close();
                    socket.close();
                }
                float max = proposals[0];
                int index = 0;

                for (int i = 0; i < proposals.length; i++)
                {
                    if (max < proposals[i])
                    {
                        max = proposals[i];
                        index = i;
                    }
                }
                Log.e(P_TAG,"CLIENT: PICKING UP ID VALUE = " + max + " for msg: "+ msgs[0] +" IMP <<<<<<<<<<<<<<<<<<<<<<<\n\n");
                // we got ID till now, time to send the message


                for(int i=0; i<test_avds;i++) {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(remotePorts[i]));
                    //socket.setSoTimeout(5*1000);
                    //socket.connect(new InetSocketAddress(10.0.2.2, Integer.parseInt(remotePorts[i])), 1000);
                    String msgToSend = msgs[0];
                    /*
                     * TODO: Fill in your client code that sends out a message.
                     */
                    //partiks code start
                    if(socket.isConnected() && (socket.isClosed() == false)){
                    }
                    else{
                        return null;
                    }
                    //new approach
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    //broadcasting message with its respective ID
                    out.println("NAVO MSG");
                    out.println(max);
                    out.println(msgToSend);
                    out.println("AAI_GAYU");
                    String temp;
                    while((temp = in.readLine())!=null){
                        if("SERVER_AAI_GAYU".equals(temp)){
                            break;
                        }
                    }
                    out.flush();
                    //partiks code end
                    out.close();
                    in.close();
                    socket.close();
                }

            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (SocketTimeoutException ste){
                //out.flush();
                Log.e(P_TAG, "SOCKET TIMEOUT HUEHUEHUEHUE ----------------");
                ste.printStackTrace();
            }
            catch (IOException e) {
                Log.e(P_TAG, "WHY IT COME HERE THOUGH ????");
                Log.e(TAG, "ClientTask socket IOException");
                e.printStackTrace();
            }
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
