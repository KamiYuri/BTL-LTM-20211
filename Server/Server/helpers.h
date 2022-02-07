#pragma once

#include<string>
#include<vector>
using namespace std;
struct room {
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
string login(string email, string password);

string show_room(vector<room> &rooms);

string join_room(string room_id, string user_id, vector<room> &rooms);
string bid(int price, string room_id, string user_id, vector<room> &rooms);

string buy_immediately(string room_id, string user_id, vector<room> &rooms);
string create_room(string item_name, string item_description, int starting_price, int buy_immediately_price, vector<room> &rooms);
