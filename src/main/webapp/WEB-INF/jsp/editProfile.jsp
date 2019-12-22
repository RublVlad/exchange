<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="edit_profile" var="rb" />
<fmt:setBundle basename="global" var="glob" />
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta http-equiv="x-ua-compatible" content="ie=edge">
  <title><fmt:message key="title" bundle="${rb}"/></title>
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
  <jsp:include page="template/links.jsp" />
  <jsp:include page="template/scripts.jsp" />
</head>
<c:set var="courier" value="COURIER" scope="page" />
<c:choose >
    <c:when test="${sessionScope.role == pageScope.courier}">
        <c:set var="command" value="update_profile_courier" scope="page" />
    </c:when>
    <c:otherwise>
        <c:set var="command" value="update_profile_client" scope="page" />
    </c:otherwise>
</c:choose>
<body>
    <jsp:include page="template/header.jsp" />
    <!--Main layout-->
    <main class="mt-5 pb-5">

        <!--Main container-->
        <div class="container-fluid align-items-center justify-content-center">
            <div class="row d-flex justify-content-center">
                <p class="h4 mb-4"><fmt:message key="main" bundle="${rb}"/></p>
            </div>
            <c:if test="${requestScope.invalid_data}">
                <div class="row d-flex justify-content-center">
                    <div class="alert alert-danger alert-dismissible fade show">
                        <fmt:message key="invalid_data" bundle="${glob}"/>
                    </div>
                </div>
            </c:if>
            <div class="row d-flex justify-content-center">
                <div class="col-lg-6 col-md-6 mb-0">
                    <form class="text-center border border-light p-5" method="POST" action="<c:url value="/controller" />">
                        <h3 class="pt-2"><fmt:message key="form.general.form_name" bundle="${rb}"/></h3>
                        <input type="hidden" name="command" value="update_profile" />

                        <div class="form-row mb-4">
                            <div class="col">
                                <input type="text" class="form-control" name="city" placeholder="<fmt:message key="form.general.city" bundle="${rb}"/>" required>
                            </div>
                            <div class="col">
                                <input type="number" class="form-control" name="age" placeholder="<fmt:message key="form.general.age" bundle="${rb}"/>" required>
                            </div>
                        </div>
                        <button class="btn btn-info my-4 btn-block" type="submit"><fmt:message key="form.general.submit" bundle="${rb}"/></button>
                    </form>
                </div>

                <!-- Offer -->
                <c:if test="${sessionScope.role == pageScope.courier}" >
                    <div class="col-lg-4 col-md-6 mb-0">
                        <form class="text-center border border-light p-5" method="POST" action="<c:url value="/controller" />">
                            <input type="hidden" name="command" value="update_offer" />
                            <h3 class="pt-2"><fmt:message key="form.offer.form_name" bundle="${rb}"/></h3>
                            <input type="text"  class="form-control mb-4"   name="transport" placeholder="<fmt:message key="form.offer.transport" bundle="${rb}"/>" required>
                            <input type="number"  class="form-control mb-4" name="price" placeholder="<fmt:message key="form.offer.price" bundle="${rb}"/>" required>
                            <button class="btn btn-info my-4 btn-block" type="submit"><fmt:message key="form.offer.submit" bundle="${rb}"/></button>
                        </form>
                    </div>
                </c:if>
            </div>


            <div class="row justify-content-center pb-5">
                <form class="text-center" method="POST" action="<c:url value="/controller" />" enctype="multipart/form-data">
                    <input type="hidden" name="command" value="update_avatar" />
                    <h3 class="pt-4"><fmt:message key="form.photo.form_name" bundle="${rb}"/></h3>
                    <div class="input-group pt-2">
                        <div class="input-group-prepend">
                            <input type="submit" class="input-group-text" id="inputGroupFileAddon01" role="button" value="<fmt:message key="form.photo.submit" bundle="${rb}"/>"/>
                        </div>
                        <div class="input-group-prepend">
                            <div class="custom-file">
                                <input type="file" class="custom-file-input" id="inputGroupFile01" name="avatar" required>
                                <label class="custom-file-label" for="inputGroupFile01"><fmt:message key="form.photo.profile_photo" bundle="${rb}"/></label>
                            </div>
                        </div>
                    </div>
                </form>
            </div>

            <div class="row d-flex justify-content-center pt-4">
                <div class="col-lg-4 col-md-6 mb-0">
                    <form class="text-center" method="POST" action="<c:url value="/controller" />">
                        <input type="hidden" name="command" value="update_wallet" />
                        <h3 class="pt-2"><fmt:message key="form.balance.form_name" bundle="${rb}"/></h3>
                        <input type="number"  class="form-control mb-4" name="balance" placeholder="<fmt:message key="form.balance.balance" bundle="${rb}"/>" required>
                        <button class="btn btn-info my-4 btn-block" type="submit"><fmt:message key="form.balance.submit" bundle="${rb}"/></button>
                    </form>
                </div>
            </div>
        </div>
        <!--Main container-->
    
    </main>
    <!--Main layout-->
    <jsp:include page="template/footer.jsp" />
</body>
</html>
