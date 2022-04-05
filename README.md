# SD_TP01
## Notes about Work: 

### Client Side:
**interface menu** 
    1. user most login first with right credentials (login option)
    2. entry without credentials (consumers can do this - other button)
    3. do a interface menu specified to user's role (server verifies role and returns role to client to show specific interface)
        3.1. - Publishers : 3.1.1. Add a topic ; 3.1.2. Consult existing topics; 3.1.3. Add news from a specific topic; 3.1.4. Consult all published news
        3.2. - Subscribers : 3.2.1. Subscribe to a topic (canrepeated according to available number of topics => 1 topicconsumed => -1 topic in list of topics) 3.2.2. Consulting news according to time interval (timeline) => IF news was archived, backup Server gives client IP and port(??? which connections does it entail???) 3.2.3. Consult last news of a certain topic(credentials verification done in server => output strewith user data)