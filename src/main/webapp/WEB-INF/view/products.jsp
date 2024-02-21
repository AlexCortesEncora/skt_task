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
<div class="container my-5">
    <div class="card">
        <div class="card-body">
            <div class="container my-1">
                <div class="col-md-12">
                    <div>
                        <table class="table table-striped table-responsive-md">
                            <thead>
                            <tr>
                                <th style="text-align: center">Name</th>
                                <th style="text-align: center">Description</th>
                                <th style="text-align: center">Price</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:if test="${products.size()==0}">
                                <tr>
                                    <td colspan="3" style="text-align: center">No record found</td>
                                </tr>
                            </c:if>
                            <c:if test="${products.size() gt 0 }">
                                <c:forEach var="product" items="${products}">
                                    <tr>
                                        <td style="text-align: left">${product.name}</td>
                                        <td style="text-align: center">${product.description}</td>
                                        <td style="text-align: right">$ ${product.price}</td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                    <p class="my-5">
                        <a href="/management_app/add-product" class="btn btn-primary">
                            <i class="fas fa-user-plus ml-2">Add Product </i>
                        </a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>