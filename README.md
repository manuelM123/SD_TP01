# SD_TP01
## Notes about Work: 

### Client Side:
**interface menu** 
* user most login first with right credentials (login option)
* register a new user and do automatic login afterwards
* entry without credentials (consumers can do this - other button)
* do a interface menu specified to user's role (server verifies role and returns role to client to show specific interface)
    * **Publishers** 
        * Add a topic ; 
        * Consult existing topics; 
        * Add news from a specific topic; 
        * Consult all published news
    * **Subscribers** 
        * Subscribe to a topic (can be repeated according to available number of topics => 1 topic consumed => -1 topic in list of topics); 
        * Consulting news according to time interval (timeline) => IF news was archived, backup Server gives client IP and port(which connections does it entail); 
        * Consult last news of a certain topic(credentials verification done in server => output streams with user data)

### Server Side:
* Accept connection from client and verifies it's role (thread to verify credentials);
* Server creates (connection thread) according to role (3 different connections/threads => Publisher, Subscriber and non registered) 
* Thread that verifies credentials of client (instantiate role)

### RMI??