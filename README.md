# java-shareit

SpringBoot, Maven, Lombok, PostgreSQL, Docker, Hibernate

## Service for sharing

Provides users the opportunity to tell what things they are ready to share, and find the right thing and rent it for a while.
The service provides the following opportunities:
 * Book a thing for certain date and to close access to it at the time of booking from other people. 
 * In case there is no necessary thing on the service, users can leave requests.
 * On request, you can add new things for sharing.
 
### Application has next actions:

#### about User
- make registration new User
- change personal information
- delete account
- get User by id
- get all Users
 
#### about Item
- add new Item
- change information about Item — only Item owner can make changes
- delete Item — only Item owner can make changes
- get all Items owner
- get Item by id
- get Item by part of name or description


To deploy an application based on a docker container, use the command docker-compose up
