<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Products</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.1/css/all.css">
</head>
<body>
<div class="container my-2">
    <div class="card">
        <div class="card-body">
            <div class="container my-5">
                <p class="my-5">
                    <a href="/management_app/add-product" class="btn btn-primary">
                        <i class="fas fa-user-plus ml-2">Add Product </i>
                    </a>
                </p>
                <div class="col-md-10">
                    <c:if test="${products.size()==0}">
                        <h2>No record found !!</h2>
                    </c:if>
                    <c:if test="${products.size() gt 0 }">
                        <div>
                            <table class="table table-striped table-responsive-md">
                                <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Description</th>
                                    <th>Price</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="product" items="${products}">
                                    <tr>
                                        <td>${product.name}</td>
                                        <td>${product.description}</td>
                                        <td>${product.price}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>