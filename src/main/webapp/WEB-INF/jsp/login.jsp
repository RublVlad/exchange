<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="login" var="rb" />
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title><fmt:message key="title" bundle="${rb}"/></title>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
    <jsp:include page="template/links.jsp" />
    <jsp:include page="template/scripts.jsp"/>

    <c:choose>
        <c:when test="${sessionScope.lang == 'ru_RU'}">
            <c:set var="langView" value="en" scope="page"/>
            <c:set var="langControl" value="en_EN" scope="page"/>
        </c:when>
        <c:otherwise>
            <c:set var="langView" value="ru" scope="page" />
            <c:set var="langControl" value="ru_RU" scope="page"/>
        </c:otherwise>
    </c:choose>
</head>
<body>

    <c:set var="page" value="Micheak"/>
    <!--Main layout-->
    <main class="mt-5">

        <!--Main container-->
        <div class="container-fluid d-flex align-items-center justify-content-center">
          <div class="row d-flex justify-content-center">

            <!-- Default form login -->
            <form class="text-center border border-light p-5" method="POST" action="<c:url value="/controller" />">
                <input type="hidden" name="command" value="login" />
                <p class="h4 mb-4"><fmt:message key="form.name" bundle="${rb}"/></p>

                  <!-- Email -->
                <input type="email" class="form-control mb-4" name="email" placeholder="<fmt:message key="form.email" bundle="${rb}"/>" >

                  <!-- Password -->
                <input type="password" class="form-control mb-4" name="password" placeholder="<fmt:message key="form.password" bundle="${rb}"/>">

                  <!-- Sign in button -->
                <button class="btn btn-info btn-block my-4" type="submit"><fmt:message key="button.submit" bundle="${rb}"/></button>

                <c:if test="${not empty requestScope.error_string}">
                    <div class="alert alert-danger alert-dismissible fade show">
                            ${requestScope.error_string}
                    </div>
                </c:if>
                  <!-- Register -->
                <p><fmt:message key="form.teaser" bundle="${rb}"/>
                    <a href="<c:url value="/jsp/register.jsp" /> "><fmt:message key="button.register" bundle="${rb}"/></a>
                </p>
                <p>
                    <a class="nav-link" href="<c:url value="/controller?command=set_locale&lang=${pageScope.langControl}&page=${pageContext.request.servletPath}" />">${pageScope.langView}</a>
                </p>
            </form>
            <!-- Default form login -->
          </div>


        </div>
        <!--Main container-->

      </main>
      <!--Main layout-->
</body>
</html>