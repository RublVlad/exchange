<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="register" var="rb" />
<fmt:setBundle basename="global" var="glob" />
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title><fmt:message key="title" bundle="${rb}"/></title>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
    <jsp:include page="template/links.jsp" />
    <jsp:include page="template/scripts.jsp"/>
</head>
<body>
 
    <!--Main layout-->
    <main class="mt-5">
  
      <!--Main container-->
      <div class="container-fluid d-flex align-items-center justify-content-center">
        <div class="row d-flex justify-content-center"> 
          
          <!-- Default form register -->
          <form class="text-center border border-light p-5" method="POST" action="<c:url value="/controller" />" >
              <input type="hidden" name="command" value="register" />
              <p class="h4 mb-4"><fmt:message key="form.form_name" bundle="${rb}"/></p>

              <div class="form-row mb-4">
                  <div class="col">
                      <!-- First name -->
                      <input type="text" class="form-control" name="name"  placeholder="<fmt:message key="form.name" bundle="${rb}"/>">
                  </div>
                  <div class="col">
                      <!-- Last name -->
                      <input type="text" class="form-control" name="surname" placeholder="<fmt:message key="form.surname" bundle="${rb}"/>">
                  </div>
              </div>

              <!-- E-mail -->
              <input type="email" class="form-control mb-4" name="email"  placeholder="<fmt:message key="form.email" bundle="${rb}"/>">

              <!-- Password -->
              <input type="password" class="form-control" name="password" placeholder="<fmt:message key="form.password" bundle="${rb}"/>" aria-describedby="defaultRegisterFormPasswordHelpBlock">
              <small class="form-text text-muted mb-4">
                  <fmt:message key="form.password.constraint" bundle="${rb}"/>
              </small>

              <p><fmt:message key="form.role" bundle="${rb}"/></p>

              <div class="form-row mb-4">
                  <div class="col">
                      <div class="custom-control custom-radio">
                          <input type="radio" class="custom-control-input" id="radio1" name="role" value="client" checked>
                          <label class="custom-control-label" for="radio1"><fmt:message key="form.role.client" bundle="${rb}"/></label>
                      </div>
                  </div>
                  <div class="col">
                      <div class="custom-control custom-radio">
                          <input type="radio" class="custom-control-input" id="radio2" name="role" value ="courier" >
                          <label class="custom-control-label" for="radio2"><fmt:message key="form.role.courier" bundle="${rb}"/></label>
                      </div>
                  </div>
              </div>

              <!-- Sign up button -->
              <button class="btn btn-info my-4 btn-block" type="submit"><fmt:message key="button.register" bundle="${rb}"/></button>

              <c:if test="${not empty requestScope.error_string}">
                  <div class="alert alert-danger alert-dismissible fade show">
                        ${requestScope.error_string}
                  </div>
              </c:if>
              <c:if test="${requestScope.invalid_data}">
                  <div class="alert alert-danger alert-dismissible fade show">
                      <fmt:message key="invalid_data" bundle="${glob}"/>
                  </div>
              </c:if>

              <p><fmt:message key="form.teaser" bundle="${rb}"/>
                  <a href="<c:url value="/jsp/login.jsp"/> "><fmt:message key="button.sign_in" bundle="${rb}"/></a>
              </p>

          </form>
          <!-- Default form register -->
          </div>
        </div>
      <!--Main container-->
  
    </main>
    <!--Main layout-->

</body>
</html>
