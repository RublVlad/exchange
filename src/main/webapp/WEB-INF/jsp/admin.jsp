<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ctg" uri="/WEB-INF/tld/custom.tld" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="admin" var="rb" />
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
<header>

    <!--Navbar-->
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
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value="/controller?command=logout" />">
                            <fmt:message key="logout" bundle="${rb}"/>
                        </a>
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
<main class="mt-5 pb-5" >
    <div class="container">
        <!--Grid row-->
        <div class="row">
            <!--Grid column-->
            <c:forEach var="elem" items="${requestScope.user_list}" varStatus="status">
                <div class="col-lg-4 col-md-6 mb-4">
                    <!--Card-->
                    <div class="card">
                        <!--Card image-->
                        <div class="view overlay">
                            <div class="embed-responsive embed-responsive-4by3">
                                <img src="<c:url value="/images?role_id=${requestScope.actor_map[elem.id].id}&role=${elem.role}" />" class="card-img-top embed-responsive-item"
                                    alt="courier">
                            </div>
                            <a href="#">
                                <div class="mask rgba-white-slight"></div>
                            </a>
                        </div>
                        <!--Card content-->
                        <div class="card-body">
                            <!--Title-->
                            <h4 class="card-title"><span>${requestScope.actor_map[elem.id].name}</span> <span>${requestScope.actor_map[elem.id].surname}</span></h4>
                            <!--Text-->
                            <p class="card-text"><span><fmt:message key="card.email" bundle="${rb}"/>: ${elem.email}</span></p>
                            <p class="card-text"><span><fmt:message key="card.role" bundle="${rb}"/>: ${elem.role}</span></p>
                            <p class="card-text"><span><fmt:message key="card.balance" bundle="${rb}"/>: ${requestScope.wallet_map[elem.id].balance}</span></p>
                            <a href="<c:url value="/controller?command=delete_user&id=${elem.id}&role=${elem.role}"/>" class="btn btn-indigo"><fmt:message key="button.delete" bundle="${rb}"/></a>
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
                    <a class="page-link" href="<c:url value="/controller?command=get_users&offset=${requestScope.navigation.offset - 1}" /> ">Previous</a>
                </li>
                <li class="page-item <ctg:attrOnCond condition="${not requestScope.navigation.hasNext}" attribute="disabled"/>">
                    <a class="page-link" href="<c:url value="/controller?command=get_users&offset=${requestScope.navigation.offset + 1}" /> ">Next</a>
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
