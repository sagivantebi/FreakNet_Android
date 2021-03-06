package com.example.freakner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import api.Mess;
import api.PostAPI;

public class ChatScreen extends AppCompatActivity {

    List<Post> listConstacts = new ArrayList<>();
    List<Message> listMessages = new ArrayList<>();
    private AppDB db;
    private PostCon p;
    private MesCon m;
    private ArrayAdapter<Post> adapter;
    private ArrayAdapter<Message> adapterMessage;
    private ListView lvContacts;
    private ListView lvMessages;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        lvContacts = findViewById(R.id.listOneContact);
        lvMessages = findViewById(R.id.ListViewMessages);
        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "ContactsDB6").allowMainThreadQueries().build();
        p = db.postCon();
        m = db.mesCon();
        int id = getIntent().getExtras().getInt("id");
        this.id = id;
        String username = getIntent().getStringExtra("username");
        String contact = getIntent().getStringExtra("contact");
        listMessages = m.getMessages(id);
       // adapterMessage = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listMessages);
        adapterMessage = new CustomListAdapterMess(getApplicationContext(), (ArrayList<Message>) listMessages,username,contact);
        lvMessages.setAdapter(adapterMessage);

        Button btn_sendMessage = findViewById(R.id.btn_sendMessage);
        btn_sendMessage.setOnClickListener(v -> {
            EditText editText = findViewById(R.id.new_message);
            if(editText.getText().toString().equals(""))
                return;
//            Message message = new Message(0, editText.getText().toString(), "text", "date", true, "", "");
            Message message = new Message(editText.getText().toString(), "text", true, id);
            insertMessage(message, username, contact);
            PostAPI postAPI = new PostAPI();
            postAPI.sendMess(getApplicationContext(), username, contact,new Mess(editText.getText().toString()));
            adapterMessage.add(message);
            this.adapterMessage.notifyDataSetChanged();
            editText.getText().clear();
//            Intent intent = new Intent(this, ChatScreen.class);
//            intent.putExtra("id",id);
//            startActivity(intent);
        });


        Post cont = p.get(id);
        listConstacts.add(cont);
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listConstacts);
        adapter = new CustomListAdapterChat(getApplicationContext(), (ArrayList<Post>) listConstacts);
        lvContacts.setAdapter(adapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        listMessages.clear();
        listMessages.addAll(m.getMessages(this.id));
        adapterMessage.notifyDataSetChanged();

    }
    protected void insertMessage(Message m, String username, String contact) {
        this.m.insert(m);
        Message mTag = new Message(m);
        mTag.setSent(false);
        mTag.chatId = db.postCon().getChatId(contact, username);
        this.m.insert(mTag);
    }
}