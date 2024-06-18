package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.EmailDao;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.PaginatedResponse;
import aor.project.innovationlab.dto.emails.EmailPageDto;
import aor.project.innovationlab.email.EmailDto;
import aor.project.innovationlab.entity.EmailEntity;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.utils.InputSanitizerUtil;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class EmailBean {

    @EJB
    private EmailDao emailDao;

    @EJB
    private SessionDao sessionDao;

    @EJB
    private UserDao userDao;


    public EmailBean() {
    }


    public PaginatedResponse<Object> getEmails(
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
            pageSize = 10;
        }
        if(orderDirection != null && !orderDirection.isEmpty() && orderField != null && !orderField.isEmpty()){
            orderDirection = orderDirection.toLowerCase();
            orderField = orderField.toLowerCase();
            validateOrderParameters(orderField, orderDirection);
        }

        String emailUser = sessionEntity.getUser().getEmail();
        PaginatedResponse<EmailEntity> emailsResponse = emailDao.findEmails(senderUser, receiverUser, groupId, id,isRead, pageNumber, pageSize, orderField, orderDirection);
        List<EmailEntity> emails = emailsResponse.getResults();

        // If the user's email does not match either the sender or receiver, return an empty array
        if (!emailUser.equals(from) && !emailUser.equals(to)) {
            return new PaginatedResponse<>(new ArrayList<>(), 0);
        }

        if(dtoType == null || dtoType.isEmpty()) {
            dtoType = "EmailPageDto";
        }

        PaginatedResponse<Object> response = new PaginatedResponse<>();
        response.setTotalPages(emailsResponse.getTotalPages());

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
        createEmailIfNotExists("ricardo@ricardo","joao@joao","Teste","Teste",1L);
        createEmailIfNotExists("admin@admin","ricardo@ricardo","Teste2","Teste2",1L);
        createEmailIfNotExists("ricardo@ricardo","admin@admin","Teste3","Teste3",2L);
        createEmailIfNotExists("admin@admin","joao@joao","Teste4","Teste4",3L);
        createEmailIfNotExists("joao@joao","admin@admin","Teste5","Teste5",3L);
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
}
