<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="profile" var="rb" />
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta http-equiv="x-ua-compatible" content="ie=edge">
  <title><fmt:message key="title" bundle="${rb}"/></title>
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
  <jsp:include page="template/links.jsp" />
  <jsp:include page="template/scripts.jsp"/>
</head>
<c:set var="courier" value="COURIER" scope="page" />
<body>
  <jsp:include page="template/header.jsp"/>
  <!--Main layout-->
  <main class="mt-5 pb-5">
      <div class="container-fluid d-flex align-items-center justify-content-center">
        <!-- Grid row -->
        <div class="row d-flex justify-content-center">

          <!-- Grid column -->
          <div class="col-lg-6 col-md-6 mb-0">

            <!-- Image -->
            <div class="view overlay rounded z-depth-2 mb-4">
              <img class="img-fluid" src="<c:url value="/images?role_id=${sessionScope.id}&role=${sessionScope.role}" />" alt="Profile photo">
              <a>
                <div class="mask rgba-white-slight"></div>
              </a>
            </div>

          </div>
          <!-- Grid column -->

          <!-- Grid column -->
          <div class="col-lg-4 col-md-6 mb-0">

            <!-- Featured news -->
            <div class="single-news mb-3">

              <h2><span>${requestScope.actor.name}</span> <span>${requestScope.actor.surname}</span></h2>
              <hr>
              <p><fmt:message key="balance" bundle="${rb}"/>: ${requestScope.actor.balance}</p>
              <a href="<c:url value="/jsp/editProfile.jsp"/> " class="btn btn-indigo"><fmt:message key="update_profile" bundle="${rb}"/></a>

            </div>
            <!-- Grid column -->
          </div>
          <!-- Grid row -->
        </div>
      </div>
  </main>
  <!--Main layout-->
<jsp:include page="template/footer.jsp" />
</body>