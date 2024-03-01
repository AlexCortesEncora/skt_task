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
            <div class="col-md-12">
                <form:form action="/management_app/save-product" method="post" modelAttribute="product">
                    <form:hidden path="id"/>
                    <div class="row">
                        <div class="form-group col-md-8">
                            <label for="name" class="col-form-label">Name</label>
                            <form:input type="text" class="form-control" id="name"
                                        path="name" placeholder="Name" required="true"/>
                        </div>
                        <div class="form-group col-md-8">
                            <label for="description" class="col-form-label">Description</label>
                            <form:input type="text" class="form-control" id="description"
                                        path="description" placeholder="Description"/>
                        </div>
                        <div class="form-group col-md-8">
                            <label for="price" class="col-form-label">Price</label>
                            <form:input type="number" min="0" step="0.01" class="form-control" id="price"
                                        path="price" placeholder="Price" required="true"/>
                        </div>
                        <div class="col-md-6">
                            <input type="submit" class="btn btn-primary" value=" Submit ">
                        </div>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>
</body>
</html>