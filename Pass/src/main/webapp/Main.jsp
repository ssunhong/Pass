<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<meta name="author" content="" />
<title>Pass</title>
<!-- Core theme CSS (includes Bootstrap)-->
<link href="css/styles.css" rel="stylesheet" />>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
</head>
<body>
    <body id="page-top">
        <!-- Navigation-->
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top" id="mainNav">
            <div class="container px-4">
                <a class="navbar-brand" href="#page-top">Start Bootstrap</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>
                <div class="collapse navbar-collapse" id="navbarResponsive">
                    <ul class="navbar-nav ms-auto">
                        <li class="nav-item"><a class="nav-link" href="#serial">Serial</a></li>
                        <li class="nav-item"><a class="nav-link" href="#user">user</a></li>
                        <li class="nav-item"><a class="nav-link" href="#status">Status</a></li>
                    </ul>
                </div>
            </div>
        </nav>
        <!-- Header-->
        <header class="bg-primary bg-gradient text-white">
            <div class="container px-4 text-center">
                <h1 class="fw-bolder">Welcome to Pass</h1>
                <p class="lead">You can check the DB.</p>
            </div>
        </header>
        <!-- About section-->
        <section id="serial">
            <div class="container px-4">
                <div class="row gx-4 justify-content-center">
                    <div class="col-lg-8">
                        <h2>Serial Key</h2>
                        <table class="table table-striped">
  							<thead>
    							<tr>
      								<th scope="col">Number</th>
      								<th scope="col">Serial Key</th>
   								 </tr>
  							</thead>
  							<tbody>
								<c:forEach var="s"  varStatus="i" items="${serialTable}">
								<tr>
									<td scope="row">${i.count}</td>
									<td>${s.number}</td>
								</tr>
								</c:forEach>
							  </tbody>
						</table>
                    </div>
                </div>
            </div>
        </section>
        <!-- Services section-->
        <section class="bg-light" id="user">
            <div class="container px-4">
                <div class="row gx-4 justify-content-center">
                    <div class="col-lg-8">
                        <h2>User</h2>
                        <table class="table table-striped">
  							<thead>
    							<tr>
      								<th scope="col">Number</th>
      								<th scope="col">Name</th>
      								<th scope="col">Device ID</th>
   								 </tr>
  							</thead>
  							<tbody>
								<c:forEach var="u"  varStatus="i" items="${userTable}">
								<tr>
									<td scope="row">${i.count}</td>
									<td>${u.name}</td>
									<td>${u.deviceID}</td>
								</tr>
								</c:forEach>
							  </tbody>
						</table>
                    </div>
                </div>
            </div>
        </section>
        <!-- Contact section-->
        <section id="status">
            <div class="container px-4">
                <div class="row gx-4 justify-content-center">
                    <div class="col-lg-8">
                        <h2>Status</h2>
                            <table class="table table-striped">
  							<thead>
    							<tr>
      								<th scope="col">Number</th>
      								<th scope="col">Device ID</th>
      								<th scope="col">Time</th>
   								 </tr>
  							</thead>
  							<tbody>
								<c:forEach var="r"  varStatus="i" items="${roomTable}">
								<tr>
									<td scope="row">${i.count}</td>
									<td>${r.deviceID}</td>
									<td>${r.date}</td>
								</tr>
								</c:forEach>
							  </tbody>
						</table>
                    </div>
                </div>
            </div>
        </section>
        <!-- Footer-->
        <footer class="py-5 bg-dark">
            <div class="container px-4"><p class="m-0 text-center text-white">Copyright &copy; Your Website 2022</p></div>
        </footer>
        <!-- Bootstrap core JS-->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        <!-- Core theme JS-->
        <script src="js/scripts.js"></script>
    </body>

</body>
</html>