#include "winsock2.h"
#include "windows.h"
#include "stdio.h"
#include "conio.h"
#include <WS2tcpip.h>
#pragma comment (lib, "Ws2_32.lib")
#include "string.h"
#include "ws2tcpip.h"
#include "fstream"
#include "iostream"
#include "string"
#include "string.h"
#include "chrono"
#include "ctime"
#include "vector"
using namespace std;

#define SUCCESS_LOGIN "10"
#define FAILED_LOGIN "11"
#define SUCCESS_SHOW_ROOM "30"
#define SUCCESS_JOIN_ROOM "40"
#define ROOM_NOT_FOUND "41"
#define SUCCESS_BID "50"
#define LOWER_THAN_CURRENT_PRICE "51"
#define SUCCESS_BUY_IMMEDIATELY "60"
#define ALREADY_SOLD "60"
#define SUCCESS_CREATE_ROOM "70"
#define INVALID_INFORMATION "71"
#define MAX_CLIENT 1024
#define MAX_ROOM 1024
#define ENDING_DELIMITER "\r\n"
#define SPLITING_DELIMITER_1 "\t\n"
#define SPLITING_DELIMITER_2 "\y\n"

struct user{
  string user_id;
  string username; 
  string password;
  SOCKET socket; 
  char client_ip[INET_ADDRSTRLEN]; 
  int client_port; 
};
vector<user> users;

struct room{
  vector<string> client_list;   //array of strings of user_id
  string room_id; 
  string item_name; 
  string item_description; 
  int starting_price; 
  int buy_immediately_price; 
  int current_price; 
  string current_highest_user;
  string owner = "-1"; //default = "-1", if item was sold then = user_id
};
vector<room> rooms;

string login(string email, string password){
    ifstream fileAcc;
    fileAcc.open("account.txt");
    if (fileAcc.is_open()) {
        string line;
        while (getline(fileAcc, line)) {
            int space=line.find(" ");
            string account = line.substr(0,space);
            string pass = line.substr(space+1);
            if (email == account && password == pass) {
                return SUCCESS_LOGIN;
            }
            else return FAILED_LOGIN;
        }
    }    
}

//int logout(int user_id){}

string show_room(vector<room> &rooms){
    string message;
    for(int i=0;i<rooms.size();i++){
        message = SUCCESS_SHOW_ROOM + rooms[i].room_id + SPLITING_DELIMITER_2 + rooms[i].item_name + SPLITING_DELIMITER_2 + rooms[i].item_description + SPLITING_DELIMITER_1;
    }
    message+=ENDING_DELIMITER;
    return message;
}

string join_room(string room_id, string user_id, vector<room> &rooms){
    for(int i=0;i<rooms.size();i++){
        if(room_id == rooms[i].room_id){
            rooms[i].client_list.push_back(user_id);
            return SUCCESS_JOIN_ROOM;
        }
    }
    return ROOM_NOT_FOUND;
    
}

string bid(int price, string room_id, string user_id, vector<room> &rooms){
    for(int i=0;i<rooms.size();i++){
        if(rooms[i].room_id == room_id){
            if(price > rooms[i].current_price){
                rooms[i].current_price = price;
                rooms[i].current_highest_user = user_id;
                return SUCCESS_BID;
            }
            else return LOWER_THAN_CURRENT_PRICE;
        }
    }
}

string buy_immediately(string room_id, string user_id, vector<room> &rooms){
    for(int i=0;i<rooms.size();i++){
        if(rooms[i].room_id == room_id){
            if(rooms[i].owner == "-1"){
                rooms[i].owner == user_id;
                return SUCCESS_BUY_IMMEDIATELY;
            }
            else return ALREADY_SOLD;
        }
    }  
}

string create_room(string item_name, string item_description, int starting_price, int buy_immediately_price, vector<room> &rooms){
    room tmp_room;
    tmp_room.item_name = item_name;
    tmp_room.item_description = item_description;
    tmp_room.starting_price = starting_price;
    tmp_room.buy_immediately_price = buy_immediately_price;
    if(tmp_room.starting_price > 0 && tmp_room.buy_immediately_price > tmp_room.starting_price){
        rooms.push_back(tmp_room);
        return SUCCESS_CREATE_ROOM;
    }
    else return INVALID_INFORMATION;
}




