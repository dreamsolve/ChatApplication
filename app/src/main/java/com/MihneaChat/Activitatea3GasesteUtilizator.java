package com.MihneaChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;

import com.MihneaChat.User.UserListAdapter;
import com.MihneaChat.User.UserObject;
import com.MihneaChat.utils.CountryToPhonePrefix;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.view.View;
import java.util.ArrayList;

public class Activitatea3GasesteUtilizator extends AppCompatActivity {
    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;
    ArrayList<UserObject> userList,contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitatea3_gaseste_utilizator);
         contactList = new ArrayList<>();
        userList = new ArrayList<>();

        initializeRecyclerView();
        getContactlist();
    }
    private void getContactlist(){
        // String ISOPrefix = getCountryIso();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null,null);
        while(phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            UserObject mContact = new UserObject(" ",name,phone);
            userList.add(mContact);
            getUserDetails(mContact);
         //   mUserListAdapter.notifyDataSetChanged();

        }
    }

    private void getUserDetails(UserObject mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query =  mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  phone = "",
                            name = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            if(childSnapshot.child("phone").getValue()!= null)
                                phone = childSnapshot.child("phone").getValue().toString();
                            if(childSnapshot.child("name").getValue()!= null)
                                name = childSnapshot.child("name").getValue().toString();
                        UserObject mUser  = new UserObject(childSnapshot.getKey(), name,phone);

                        if(name.equals(phone))
                                for(UserObject mContactIterator : userList){
                                    if(mContactIterator.getPhone().equals(mUser.getPhone())){
                                        mUser.setName(mContactIterator.getName());
                                    }

                                }

                        userList.add(mUser);
                       mUserListAdapter.notifyDataSetChanged();
                        return;

                    }
                }
            }

           @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // private String getCountryIso(){
    //    String iso = null;
    // TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
    //  if(telephonyManager.getNetworkCountryIso()!=null)
    //   if(!telephonyManager.getNetworkCountryIso().toString().equals(""))
    //     iso=telephonyManager.getNetworkCountryIso().toString();
    //return CountryToPhonePrefix.getPhone(iso);

    //}
//
    private void initializeRecyclerView() {
        mUserList = findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext()  ,RecyclerView.VERTICAL  , false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);

    }
}
