package teammates.ui.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;

public class AjaxResult extends ActionResult {

    public PageData data;
    
    public AjaxResult(String destination, 
                      AccountAttributes account, 
                      Map<String, String[]> parametersFromPreviousRequest, 
                      List<StatusMessage> status) {
        super(destination, account, parametersFromPreviousRequest, status);
    }

    public AjaxResult(AccountAttributes account,
                      Map<String, String[]> parametersFromPreviousRequest,
                      List<StatusMessage> status, 
                      PageData data) {
        super("", account, parametersFromPreviousRequest, status);
        this.data = data;
    }
    
    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        
        req.setAttribute(Const.ParamsNames.ERROR, "" + isError);
        addStatusMessagesToPageData(req);
        clearStatusMessageForRequest(req);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String jsonData = (new Gson()).toJson(data);
        
        resp.getWriter().write(jsonData);    
    } 

    /**
     * Adds the list of status messages (if any) to the page data.
     * @param req HttpServletRequest object
     */
    private void addStatusMessagesToPageData(HttpServletRequest req) {
        List<StatusMessage> statusMessagesToUser = (List<StatusMessage>) req.getSession().getAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST);
        
        // If the list of status messages can be found in the session and it is not empty,
        // means there are status messages to be shown to the user, add them to the page data.
        if (statusMessagesToUser != null && !statusMessagesToUser.isEmpty()) {
            req.getSession().removeAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST);
            data.setStatusMessagesToUser(statusMessagesToUser);
        }
    }
    
    private void clearStatusMessageForRequest(HttpServletRequest req) {
        List<StatusMessage> statusMessagesToUser = (List<StatusMessage>) req.getSession().getAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST);
        
        if (statusMessagesToUser != null) {
            req.getSession().removeAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST);
        }
    }
}
