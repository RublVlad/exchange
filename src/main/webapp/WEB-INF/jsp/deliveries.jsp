<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ctg" uri="/WEB-INF/tld/custom.tld" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="deliveries" var="rb" />
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
<c:set var="courier" value="COURIER" scope="page" />
<c:choose >
  <c:when test="${sessionScope.role == pageScope.courier}">
    <c:set var="list" value="${requestScope.client_list}" scope="page" />
    <c:set var="role" value="CLIENT" scope="page" />
    <c:set var="command" value="command=finish_delivery&courierId=${sessionScope.id}" />
    <c:set var="otherId" value="clientId" />
    <c:set var="meFinished" value="courierFinished" />
  </c:when>
  <c:otherwise>
    <c:set var="list" value="${requestScope.courier_list}" scope="page" />
    <c:set var="role" value="COURIER" scope="page" />
    <c:set var="command" value="command=finish_delivery&clientId=${sessionScope.id}" scope="page" />
    <c:set var="otherId" value="courierId" scope="page"/>
    <c:set var="meFinished" value="clientFinished" scope="page" />
  </c:otherwise>
</c:choose>
<body>
<jsp:include page="template/header.jsp" />
<main class="mt-5 pb-5">
  <div class="container">
    <!--Grid row-->
    <div class="row">
      <!--Grid column-->
      <c:forEach var="elem" items="${pageScope.list}" varStatus="status">
        <c:choose>
          <c:when test="${pageScope.role == 'CLIENT'}" >
            <c:set var="check" value="${requestScope.delivery_list[status.index].courierFinished}" scope="page" />
          </c:when>
          <c:otherwise>
            <c:set var="check" value="${requestScope.delivery_list[status.index].clientFinished}" scope="page" />
          </c:otherwise>
        </c:choose>

        <div class="col-lg-4 col-md-6 mb-4">
          <!--Card-->
          <div class="card">
            <!--Card image-->
            <div class="view overlay">
              <div class="embed-responsive embed-responsive-4by3">
                <img src="<c:url value="/images?role_id=${elem.id}&role=${pageScope.role}" />" class="card-img-top embed-responsive-item"
                     alt="courier">
              </div>
              <a href="${check}">
                <div class="mask rgba-white-slight"></div>
              </a>
            </div>
            <!--Card content-->
            <div class="card-body">
              <!--Title-->
              <h4 class="card-title"><span>${elem.name}</span> <span>${elem.surname}</span></h4>
              <!--Text-->
              <a href="<c:url value="/controller?${pageScope.command}&${pageScope.otherId}=${elem.id}"/>" class="btn btn-indigo <ctg:attrOnCond attribute="disabled" condition="${pageScope.check}" />" ><fmt:message key="button" bundle="${rb}"/></a>
            </div>
          </div>
          <!--/.Card-->
        </div>
        <!--Grid column-->
      </c:forEach>
    </div>
    <!--Grid row-->
    <nav>
      <ul class="pagination pg-blue justify-content-center">
        <li class="page-item <ctg:attrOnCond condition="${not requestScope.navigation.hasPrevious}" attribute="disabled"/>">
          <a class="page-link" href="<c:url value="/controller?command=get_deliveries&offset=${requestScope.navigation.offset - 1}" /> ">Previous</a>
        </li>
        <li class="page-item <ctg:attrOnCond condition="${not requestScope.navigation.hasNext}" attribute="disabled"/>">
          <a class="page-link" href="<c:url value="/controller?command=get_deliveries&offset=${requestScope.navigation.offset + 1}" /> ">Next</a>
        </li>
      </ul>
    </nav>
  </div>
  <!--Main container-->

</main>
<!--Main layout-->
<jsp:include page="template/footer.jsp" />
</body>
</html>
