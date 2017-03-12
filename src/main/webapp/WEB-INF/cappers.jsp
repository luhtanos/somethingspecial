<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="include.jsp"/>
    <title>Home page</title>
    <script>
        $(document).ready(function () {
            $("#update-cappers").click(function () {
                $(this).prop('disabled', true);
                $.ajax({
                    url: "/updateCappers",
                    success: function () {
                        setTimeout(function() {
                            window.location.reload();
                        }, 100); //????
                    },
                    error: function (jqxhr) {
                        console.log(jqxhr.responseText);
                        $(this).prop('disabled', false);
                    }
                })
            });
        });
    </script>
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="/">Shitty</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li><a href="/">Home</a></li>
                <li class="active"><a href="">Cappers</a></li>
                <li><a href="matches">Matches</a></li>
                <li><a href="about">About</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container">
    <div class="row">
        <div class="col-lg-12">
            <h1>Cappers!</h1>
            <button id="update-cappers" class="btn btn-secondary right">Update cappers</button>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-12">
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th rowspan="2">League</th>
                            <th rowspan="2">Name</th>
                            <th colspan="4">Overall</th>
                            <th colspan="4">Sides</th>
                            <th colspan="4">O/U</th>
                        </tr>
                        <tr>
                            <th>Won</th>
                            <th>Lost</th>
                            <th>Tied</th>
                            <th>%</th>
                            <th>Won</th>
                            <th>Lost</th>
                            <th>Tied</th>
                            <th>%</th>
                            <th>Won</th>
                            <th>Lost</th>
                            <th>Tied</th>
                            <th>%</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${cappers}" var="capper">
                            <td>${capper.league}</td>
                            <td>${capper.name}</td>
                            <td>${capper.won}</td>
                            <td>${capper.lost}</td>
                            <td>${capper.tied}</td>
                            <td>${capper.percentWinning}</td>
                            <td>${capper.sidesWon}</td>
                            <td>${capper.sidesLost}</td>
                            <td>${capper.sidesTied}</td>
                            <td>${capper.percentWinningSides}</td>
                            <td>${capper.totalsWon}</td>
                            <td>${capper.totalsLost}</td>
                            <td>${capper.totalsTied}</td>
                            <td>${capper.percentWinningTotals}</td>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
