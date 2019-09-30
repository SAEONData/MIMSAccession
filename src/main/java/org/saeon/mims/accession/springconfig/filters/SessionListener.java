package org.saeon.mims.accession.springconfig.filters;

import lombok.extern.slf4j.Slf4j;
import org.saeon.mims.accession.model.user.User;
import org.saeon.mims.accession.service.user.UserService;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Slf4j
public class SessionListener implements HttpSessionListener {

    private final UserService userService;

    public SessionListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        log.info("Session created for id: " + httpSessionEvent.getSession().getId().substring(0, 5) + "....");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        String sessionId = httpSessionEvent.getSession().getId();
        log.info("Session destroyed for id: {}", sessionId);
        User user = userService.getUserByAuthToken(sessionId);
        if (user != null) {
            user.setAuthToken(null);
            userService.updateUser(user);
            log.info("User id [{}] session terminated.", user.getId());
        }
    }
}
