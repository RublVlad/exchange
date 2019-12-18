<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ctg" uri="/WEB-INF/tld/custom.tld" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="offers" var="rb" />
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta http-equiv="x-ua-compatible" content="ie=edge">
  <title><fmt:message key="title" bundle="${rb}"/></title>
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
  <jsp:include page="template/links.jsp" />
  <jsp:include page="template/scripts.jsp"/>
</head>
<c:set var="role" value="COURIER" scope="page"/>
<body>
<jsp:include page="template/header.jsp" />
<main class="mt-5 pb-5" >
  <c:choose >
    <c:when test="${sessionScope.role == 'COURIER'}">
      <div class="container-fluid d-flex align-items-center justify-content-center">
        <!-- Grid row -->
    </c:when>
    <c:otherwise>
        <div class="container">
          <c:if test="${not empty requestScope.error_string}">
            <div class="row d-flex justify-content-center">
              <div class="alert alert-danger alert-dismissible fade show">
                  ${requestScope.error_string}
              </div>
            </div>
          </c:if>
          <!--Grid row-->
          <div class="row">
    </c:otherwise>
  </c:choose>
          <!--Grid column-->
          <c:forEach var="elem" items="${requestScope.offer_list}" varStatus="status">
            <div class="col-lg-4 col-md-6 mb-4">
              <!--Card-->
              <div class="card">
                <!--Card image-->
                <div class="view overlay">
                  <div class="embed-responsive embed-responsive-4by3">
                    <img src="<c:url value="/images?role_id=${elem.courierId}&role=${pageScope.role}" />" class="card-img-top embed-responsive-item"
                         alt="courier">
                  </div>
                  <a href="#">
                    <div class="mask rgba-white-slight"></div>
                  </a>
                </div>
                <!--Card content-->
                <div class="card-body">
                  <!--Title-->
                  <h4 class="card-title"><span>${requestScope.actor_list[status.index].name}</span> <span>${requestScope.actor_list[status.index].surname}</span></h4>
                  <!--Text-->
                  <p class="card-text"><span><fmt:message key="card.price" bundle="${rb}"/>: ${elem.price}</span></p>
                  <p class="card-text"><span><fmt:message key="card.transport" bundle="${rb}"/>: ${elem.transport}</span></p>
                  <p class="card-text"><span><fmt:message key="card.likes" bundle="${rb}"/>: ${requestScope.actor_list[status.index].likes}</span></p>
                  <c:if test="${sessionScope.role == 'CLIENT'}">
                    <a href="<c:url value="/controller?command=request_delivery&courierId=${elem.courierId}&clientId=${sessionScope.id}"/>" class="btn btn-indigo"><fmt:message key="button.request_delivery" bundle="${rb}"/></a>
                    <a href="<c:url value="/controller?command=like_courier&courierId=${elem.courierId}&relation=like"/>" class="<ctg:attrOnCond attribute="disabled" condition="${requestScope.relation_list[status.index].relation == 'LIKE'}" />"><i class="fa fa-thumbs-up"></i></a>
                  </c:if>
                </div>
              </div>
              <!--/.Card-->
            </div>
            <!--Grid column-->
          </c:forEach>
        </div>
        <!--Grid row-->
      </div>


  <!--Main container-->
</main>
<!--Main layout-->
<jsp:include page="template/footer.jsp" />
</body>
</html>
