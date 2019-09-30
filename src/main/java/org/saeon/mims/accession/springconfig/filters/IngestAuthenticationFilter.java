package org.saeon.mims.accession.springconfig.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.saeon.mims.accession.model.user.User;
import org.saeon.mims.accession.service.user.UserService;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class IngestAuthenticationFilter extends GenericFilterBean {

    private final UserService userService;

    public IngestAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        final String authHeader = request.getHeader("authorization");

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);

            chain.doFilter(req, res);
        } else {
            String authToken = null;
            if (request.getCookies() != null) {
                List<Cookie> cookies = Arrays.asList(request.getCookies());
                if (cookies != null && !cookies.isEmpty()) {
                    Optional<Cookie> cookie = Arrays.asList(request.getCookies()).stream().filter(c -> c.getName().equalsIgnoreCase("mims-accession")).findAny();
                    if (cookie.isPresent()) {
                        authToken = cookie.get().getValue();
                    }
                }
            }

            if (StringUtils.isEmpty(authToken)) {
                log.info("Current session: {}", authHeader);
                HttpSession session = request.getSession();

                log.info(session.getId());
                authToken = session.getId();
            }

            final User user = userService.getUserByAuthToken(authToken);
            if (user == null) {
                log.info("No user found, throwing 403");
                response.sendError(403,"Token not registered");
                return;
            }

            chain.doFilter(req, res);

        }
    }
}
