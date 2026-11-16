<%@ page contentType="application/json; charset=UTF-8" isErrorPage="true" session="false" %><%
    response.setStatus(401);
    response.setHeader("WWW-Authenticate", "Basic realm=\"GalenoSV\"");
    response.addHeader("Vary", "Origin");
    if ("http://localhost:3000".equals(request.getHeader("Origin"))) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
    }
%>{"status_code":401,"error_code":"UNAUTHORIZED","message":"Se requiere una cuenta válida de GalenoSV."}