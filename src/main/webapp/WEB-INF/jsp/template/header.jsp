<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!--Main Navigation-->
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="header" var="rb" />
<header>
  <!--Navbar-->
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
  <nav class="navbar navbar-expand-lg navbar-dark indigo">

    <!-- Additional container -->
    <div class="container">

      <!-- Navbar brand -->
      <a class="navbar-brand" href="#">Marlin</a>

      <!-- Collapse button -->
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#basicExampleNav"
              aria-controls="basicExampleNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <!-- Collapsible content -->
      <div class="collapse navbar-collapse" id="basicExampleNav">

        <!-- Links -->
        <ul class="navbar-nav mr-auto">
          <li class="nav-item">
            <a class="nav-link" href="<c:url value="/jsp/profile.jsp" />"><fmt:message key="link.profile" bundle="${rb}"/></a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="<c:url value="/jsp/editProfile.jsp" />"><fmt:message key="link.edit_profile" bundle="${rb}"/></a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="<c:url value="/jsp/offers.jsp" />"><fmt:message key="link.offers" bundle="${rb}"/></a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="<c:url value="/jsp/deliveries.jsp" />"><fmt:message key="link.deliveries" bundle="${rb}"/></a>
          </li>
        </ul>
        <ul class="navbar-nav">
          <li class="nav-item">
            <a class="nav-link" href="<c:url value="/controller?command=set_locale&lang=${pageScope.langControl}&page=${pageContext.request.servletPath}" />">${pageScope.langView}</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="<c:url value="/controller?command=logout" />"><fmt:message key="link.logout" bundle="${rb}"/></a>
          </li>
        </ul>
        <!-- Links -->

      </div>
      <!-- Collapsible content -->

    </div>
    <!-- Additional container -->

  </nav>
  <!--/.Navbar-->

  </header>
  <!--Main Navigation-->