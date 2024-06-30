package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.EmailDao;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.response.PagAndUnreadResponse;
import aor.project.innovationlab.dto.emails.EmailPageDto;
import aor.project.innovationlab.dto.emails.EmailResponseDto;
import aor.project.innovationlab.dto.emails.EmailSendDto;
import aor.project.innovationlab.entity.EmailEntity;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.NotificationType;
import aor.project.innovationlab.utils.InputSanitizerUtil;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import aor.project.innovationlab.websocket.bean.HandleWebSockets;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Stateless
public class EmailBean {

    @EJB
    private EmailDao emailDao;

    @EJB
    private SessionDao sessionDao;

    @EJB
    private UserDao userDao;

    @Inject
    private SessionBean sessionBean;

    @Inject
    private HandleWebSockets handleWebSockets;

    @Inject
    private WebSocketBean webSocketBean;

    @Inject
    private NotificationBean notificationBean;


    public EmailBean() {
    }


    public PagAndUnreadResponse<Object> getEmails(
            String dtoType,
            String from,
            String to,
            Long groupId,
            Long id,
            Boolean isRead,
            Integer pageNumber,
            Integer pageSize,
            String orderField,
            String orderDirection,
            String searchText,
            String token){
        String log = "Attempt to get emails";
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null){

            LoggerUtil.logError(log,"Session not found.",null,token);
            throw new IllegalArgumentException("Session not found.");
        }

        // Validate inputs
        from = InputSanitizerUtil.sanitizeInput(from);
        to = InputSanitizerUtil.sanitizeInput(to);

        // Fetch UserEntity for sender and receiver
        UserEntity senderUser = userDao.findUserByEmail(from);
        UserEntity receiverUser = userDao.findUserByEmail(to);

        if(pageNumber == null || pageNumber < 0){
            pageNumber = 1;
        }
        if(pageSize == null || pageSize < 0){
            pageSize = 5;
        }
        if(orderDirection != null && !orderDirection.isEmpty() && orderField != null && !orderField.isEmpty()){
            orderDirection = orderDirection.toLowerCase();
            orderField = orderField.toLowerCase();
            validateOrderParameters(orderField, orderDirection);
        }

        String emailUser = sessionEntity.getUser().getEmail();
        PagAndUnreadResponse<EmailEntity> emailsResponse = emailDao.findEmails(senderUser, receiverUser, groupId, id,isRead, pageNumber, pageSize, orderField, orderDirection, emailUser, searchText);
        List<EmailEntity> emails = emailsResponse.getResults();

        // If the user's email does not match either the sender or receiver, return an empty array
        if (!emailUser.equals(from) && !emailUser.equals(to)) {
            return new PagAndUnreadResponse<>(new ArrayList<>(),0,0L);
        }


        if(dtoType == null || dtoType.isEmpty()) {
            dtoType = "EmailPageDto";
        }

        PagAndUnreadResponse<Object> response = new PagAndUnreadResponse<>();
        response.setTotalPages(emailsResponse.getTotalPages());
        response.setUnreadCount(emailsResponse.getUnreadCount());

        switch(dtoType) {

            case "EmailPageDto":
                response.setResults(emails.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()));
                break;
            default:
                response.setResults(new ArrayList<>());
                break;
        }
        return response;
    }

    public long getUnreadEmails(String email){
        return emailDao.countUnreadEmails(email);
    }

    private EmailPageDto toDto(EmailEntity emailEntity) {
        EmailPageDto dto = new EmailPageDto();
        dto.setId(emailEntity.getId());
        dto.setFrom(emailEntity.getSender().getEmail());
        dto.setTo(emailEntity.getReceiver().getEmail());
        dto.setSubject(emailEntity.getSubject());
        dto.setSentDate(emailEntity.getSentDate());
        dto.setBody(emailEntity.getBody());
        dto.setGroupId(emailEntity.getGroupId());
        dto.setRead(emailEntity.isRead());
        dto.setFromName(emailEntity.getSender().getFirstname());
        dto.setImgSrc(emailEntity.getSender().getProfileImagePath());
        return dto;
    }
    private EmailResponseDto responseToDto(EmailEntity emailEntity) {
        EmailResponseDto dto = new EmailResponseDto();
        dto.setFrom(emailEntity.getSender().getEmail());
        dto.setTo(emailEntity.getReceiver().getEmail());
        dto.setGroupId(emailEntity.getGroupId());
        dto.setBody(emailEntity.getBody());
        dto.setId(emailEntity.getId());
        return dto;
    }
    private EmailEntity responseToEntity(EmailResponseDto emailResponseDto) {
        EmailEntity entity = new EmailEntity();
        entity.setSender(userDao.findUserByEmail(emailResponseDto.getFrom()));
        entity.setReceiver(userDao.findUserByEmail(emailResponseDto.getTo()));
        entity.setSubject(emailResponseDto.getSubject());
        entity.setBody(emailResponseDto.getBody());
        entity.setGroupId(emailResponseDto.getGroupId());
        return entity;
    }

    private EmailEntity toEntity(EmailPageDto emailPageDto) {
        EmailEntity entity = new EmailEntity();
        entity.setId(emailPageDto.getId());
        entity.setSender(userDao.findUserByEmail(emailPageDto.getFrom()));
        entity.setReceiver(userDao.findUserByEmail(emailPageDto.getTo()));
        entity.setSubject(emailPageDto.getSubject());
        entity.setSentDate(emailPageDto.getSentDate());
        entity.setBody(emailPageDto.getBody());
        entity.setGroupId(emailPageDto.getGroupId());
        entity.setRead(emailPageDto.isRead());
        return entity;
    }

    private void validateOrderParameters(String orderField, String orderDirection) {
        if(!orderField.equals("from") && !orderField.equals("to") && !orderField.equals("subject") && !orderField.equals("date") && !orderField.equals("content")){
            throw new IllegalArgumentException("Invalid order field.");
        }
        if(!orderDirection.equals("asc") && !orderDirection.equals("desc")){
            throw new IllegalArgumentException("Invalid order direction.");
        }
    }

    public void createInitialData() {
        Random random = new Random();
        String[] emails = {"ricardo@ricardo", "joao@joao", "admin@admin"};
        String[] subjects = {"Teste", "Teste2", "Teste3", "Teste4", "Teste5"};
        String[] bodies = {"Corpo do email 1", "Corpo do email 2", "Corpo do email 3", "Corpo do email 4", "Corpo do email 5"};

        for (int i = 0; i < 50; i++) {
            String sender = emails[random.nextInt(emails.length)];
            String receiver;
            do {
                receiver = emails[random.nextInt(emails.length)];
            } while (receiver.equals(sender)); // Ensure sender and receiver are not the same

            String subject = subjects[random.nextInt(subjects.length)];
            String body = bodies[random.nextInt(bodies.length)];
            Long groupId = (long) (random.nextInt(5) + 1);

            createEmailIfNotExists(sender, receiver, subject, body, groupId);
        }
    }

    public void createEmailIfNotExists(String emailSender, String emailReceiver, String subject, String body, Long groupId) {
        System.out.println("Creating email if not exists");
        EmailPageDto emailPageDto = new EmailPageDto();
        emailPageDto.setFrom(emailSender);
        emailPageDto.setTo(emailReceiver);
        emailPageDto.setSubject(subject);
        emailPageDto.setBody(body);
        emailPageDto.setGroupId(groupId);

        EmailEntity emailEntity = toEntity(emailPageDto);
        emailDao.persist(emailEntity);
    }

    public EmailPageDto markMailAsRead(Long id, String token) {
        String log = "Attempt to mark email as read";
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null){
            LoggerUtil.logError(log,"Session not found.",null,token);
            throw new IllegalArgumentException("Session not found.");
        }
        EmailEntity emailEntity = emailDao.findEmailById(id);
        if(emailEntity == null){
            LoggerUtil.logError(log,"Email not found.",null,token);
            throw new IllegalArgumentException("Email not found.");
        }
        if(!emailEntity.getReceiver().getEmail().equals(se.getUser().getEmail()) && !emailEntity.getSender().getEmail().equals(se.getUser().getEmail())){
            LoggerUtil.logError(log,"User not allowed to mark email as read.",null,token);
            throw new IllegalArgumentException("User not allowed to mark email as read.");
        }
        if(emailEntity.getSender().getEmail().equalsIgnoreCase(se.getUser().getEmail())){
            return toDto(emailEntity);
        }
        emailEntity.setRead(true);
        emailDao.merge(emailEntity);
        return toDto(emailEntity);
    }

    public EmailPageDto deleteEmail(Long id, String token) {
        String log = "Attempt to delete email";
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null){
            LoggerUtil.logError(log,"Session not found.",null,token);
            throw new IllegalArgumentException("Session not found.");
        }
        EmailEntity emailEntity = emailDao.findEmailById(id);
        if(emailEntity == null){
            LoggerUtil.logError(log,"Email not found.",null,token);
            throw new IllegalArgumentException("Email not found.");
        }
        if(emailEntity.getReceiver().getEmail().equals(se.getUser().getEmail())){
            emailEntity.setDeletedByReceiver(true);
        } else if(emailEntity.getSender().getEmail().equals(se.getUser().getEmail())){
            emailEntity.setDeletedBySender(true);
        } else {
            LoggerUtil.logError(log,"User not allowed to delete email.",null,token);
            throw new IllegalArgumentException("User not allowed to delete email.");
        }
        emailDao.merge(emailEntity);

        return toDto(emailEntity);
    }

    public EmailResponseDto sendEmailResponse(Long id, EmailResponseDto emailDto, String token) {
        String log= "Attempt to send email response";
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null){
            LoggerUtil.logError(log,"Session not found.",null,token);
            throw new IllegalArgumentException("Session not found.");
        }
        EmailEntity emailEntity = emailDao.findEmailById(id);
        if(emailEntity == null){
            LoggerUtil.logError(log,"Email not found.",null,token);
            throw new IllegalArgumentException("Email not found.");
        }
        if(!emailEntity.getReceiver().getEmail().equals(se.getUser().getEmail())){
            LoggerUtil.logError(log,"User not allowed to send email response.",null,token);
            throw new IllegalArgumentException("User not allowed to send email response.");
        }
        EmailResponseDto responseToDto = new EmailResponseDto();
        responseToDto.setFrom(emailEntity.getReceiver().getEmail());
        responseToDto.setTo(emailEntity.getSender().getEmail());
        // Check if subject already starts with "Re:"
        if (emailEntity.getSubject().startsWith("Re: ")) {
            responseToDto.setSubject(emailEntity.getSubject());
        } else {
            responseToDto.setSubject("Re: " + emailEntity.getSubject());
        }
        responseToDto.setGroupId(emailEntity.getGroupId());
        responseToDto.setId(emailEntity.getId());
        String originalEmailBody = "<hr/> -- original -- <hr/>" + emailEntity.getBody();
        responseToDto.setBody(emailDto.getBody() + originalEmailBody);
        responseToDto.setGroupId(emailEntity.getGroupId());
        EmailEntity responseEntity = responseToEntity(responseToDto);
        emailDao.persist(responseEntity);

        notificationBean.sendNotification(emailEntity.getReceiver().getEmail(), emailEntity.getSender().getEmail(), "You have received an email from " + emailEntity.getReceiver().getEmail(), NotificationType.NEW_MAIL, null);

        return emailDto;
    }

    public void sendEmailInviteToUser(String token, String invitedUserEmail, String subject, String body, Long id) {
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null){
            throw new IllegalArgumentException("Session not found.");
        }

        String from = sessionEntity.getUser().getEmail();

        UserEntity userEntity = userDao.findUserByEmail(invitedUserEmail);

        if(userEntity == null){
            throw new IllegalArgumentException("User not found.");
        }

        String to = userEntity.getEmail();

        EmailPageDto emailDto = new EmailPageDto();
        emailDto.setFrom(from);
        emailDto.setTo(to);
        emailDto.setSubject(subject);
        emailDto.setBody(body);

        EmailEntity emailEntity = toEntity(emailDto);
        emailDao.persist(emailEntity);

//        notificationBean.sendNotification(sessionBean.getUserByToken(token).getEmail(), projectInviteDto.getInvitedUserEmail(), "You have been invited to join the project " + project.getName(),NotificationType.INVITE, project.getId());

        notificationBean.sendNotification(from, to, "You have received an email from " + from, NotificationType.NEW_MAIL, id);
    }

    public String createEmailBody( String projectName,String projectLink, String acceptLink, String rejectLink) {
        String body = "<p>You're invited to join the project <a href='" + projectLink + "'>"+projectName+"</a></p>" +
                "<p><a href='" + acceptLink + "'>Accept the invitation</a></p>" +
                "<p><a href='" + rejectLink + "'>Reject the invitation</a></p>";
        return body;
    }

    public void sendMailToUser(String token, String to, String subject, String body) {
        System.out.println("Sending email");
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null){
            throw new IllegalArgumentException("Session not found.");
        }

        UserEntity fromUser = sessionEntity.getUser();
        UserEntity toUser = userDao.findUserByEmail(to);
        if(toUser == null){
            throw new IllegalArgumentException("User not found.");
        }

        EmailSendDto emailDto = new EmailSendDto(body, to, subject);

        EmailEntity emailEntity = emailSendtoEntity(emailDto, fromUser);
        emailDao.persist(emailEntity);

        notificationBean.sendNotification(fromUser.getEmail(), to, "You have received an email from " + fromUser.getEmail(), NotificationType.NEW_MAIL, null);
    }

    private EmailEntity emailSendtoEntity(EmailSendDto emailDto, UserEntity fromUser){
        EmailEntity entity = new EmailEntity();
        entity.setSender(fromUser);
        entity.setReceiver(userDao.findUserByEmail(emailDto.getTo()));
        entity.setSubject(emailDto.getSubject());
        entity.setBody(emailDto.getBody());
        System.out.println("Email entity" );
        return entity;
    }


}
