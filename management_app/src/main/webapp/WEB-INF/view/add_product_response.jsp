<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Add Product</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.1/css/all.css">
</head>
<body>
<div class="container my-5">
    <div class="card">
        <div class="card-tittle">
            <div class="container">
                <div class="col-md-12">
                    <h1>Add Product</h1>
                </div>
            </div>
        </div>
        <div class="card-body">
            <div class="container my-1">
                <div class="col-md-12">
                    <c:if test="${status eq 'ERROR'}">
                        <div class="col-md-12">
                            <div class="alert alert-danger alert-dismissible fade show">
                                <strong>Error!</strong> ${message}
                            </div>
                            <p class="my-5">
                                <a href="/management_app/add-product" class="btn btn-primary">
                                    <i class="fas fa-user-plus ml-2"> Go back </i>
                                </a>
                            </p>
                        </div>
                    </c:if>
                    <c:if test="${status eq 'SUCCESS'}">
                        <div class="col-md-8">
                            <div class="alert alert-success alert-dismissible fade show">
                                <strong>Successful!</strong> ${message}
                            </div>
                            <p class="my-5">
                                <a href="/management_app/products" class="btn btn-primary">
                                    <i class="fa fa-book ml-2"> Go to Product Catalog </i>
                                </a>
                            </p>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>