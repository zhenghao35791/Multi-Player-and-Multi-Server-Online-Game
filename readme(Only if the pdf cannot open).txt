*£¨If possible, please have a look at "readme.pdf" instead of "txt" because the "readme.txt" doesnt have images.£©



Start Eclipse
Import the project. Choose "Existing Projects into Workspace"
 
Select the Source Code location (in this example, I copy the source code to the desktop) 
 

Start the main Server (Run the ¡°TankWarMainServer.java¡±).

 

If showing the following page of ¡°refuse connect to CouchDB¡±, then you have to download the CouchDB. CouchDB is a database that uses JSON for documents. I have put the installation packages in the CD, including a version for Mac and a version for windows. (Or just google ¡°CouchDb download¡±). 
 
After installation you have to click to this following page, only when you can get access to this page, means the database is active. (Maybe register is required)
http://127.0.0.1:5984/_utils/index.html  and you should see some databases like the following page show.

 


Start the client (Run the ¡°TankClient¡±). At this time, Player ID at the top left corner is still 0.
Press ¡°C¡± on the keyboard to calling the Connecting Dialog to enter the Server IP and port. (If use ¡°localhost¡± don¡¯t need to change, just click ¡°YES¡± or press¡°ENTER¡± because the ¡°127.0.0.1¡± and port number are already input in the dialog)
When the player ID (at the top left corner) is not 0, means the player has connect to server.

 
 
TEST GAME ON ONE COMPUTER
If you want to play this game with two players (on local host), then run another ¡±TankClient¡±, press ¡°C¡± on the keyboard, then change the ¡°MyUDPPort¡± (in the popping dialog) to another number( not the same as the previous client).
  

TEST GAME ON MULTI COMPUTERS

Because it is a multi-client game, you can also play it on different computers, you can see from the game demo in the CD.
You have to use a computer as the server and get its IP (not 127.0.0.1 anymore). Firstly, change the value ¡°MainServerIP¡± in ¡°TankWarMainServer.java¡± to this new IP (open Source Code (Hao Zheng 594644) /src/ TankWarMainServer.java and change the value).
 
Secondly, when you input the server¡¯s IP in the game dialog (press ¡°C¡±), you need to change the server IP to this new IP, then the client will connect to the server.

Similarly, you can start a backup server by running ¡°TankWarBackupServer.java¡±, you still have to change the value ¡°BACKUP_SERVER_IP¡± to the computer¡¯s IP who act as the new backup server. 

 
The value of ¡°MAX_CLIENTS¡± means the max client amount that the server can handle; I have set this value to 20 for both the main server and the backup server.
