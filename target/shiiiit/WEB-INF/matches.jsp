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
                $("#update-matches").click(function () {
                    var that = $(this);
                    that.prop('disabled', true);
                    $.ajax({
                        url: "/updateMatches",
                        success: function () {
                            setTimeout(function() {
                                window.location.reload();
                            }, 100); //????
                        },
                        error: function (jqxhr) {
                            console.log(jqxhr.responseText);
                            that.prop('disabled', false);
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
                        <li><a href="cappers">Cappers</a></li>
                        <li class="active"><a href="">Matches</a></li>
                        <li><a href="about">About</a></li>
                    </ul>
                </div>
            </div>
        </nav>

        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <h1>Matches!</h1>
                    <button id="update-matches" class="btn btn-secondary right">Update matches</button>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-6">
                    <h3>Latest matches</h3>
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>League</th>
                                    <th>Time</th>
                                    <th>Away team</th>
                                    <th>Home team</th>
                                    <th>Score</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${latestMatches}" var="match">
                                    <tr>
                                        <td>${match.league.name}</td>
                                        <td><fmt:formatDate value="${match.date}" pattern="dd.MM.yyyy hh:mm"/></td>
                                        <td>${match.awayTeam.name}</td>
                                        <td>${match.homeTeam.name}</td>
                                        <td>${match.awayScore}:${match.homeScore}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="col-lg-6">
                    <h3>Upcoming matches</h3>
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                            <tr>
                                <th>League</th>
                                <th>Time</th>
                                <th>Away team</th>
                                <th>Home team</th>
                                <th>Score</th>
                            </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${upcomingMatches}" var="match">
                                    <tr>
                                        <td>${match.league.name}</td>
                                        <td><fmt:formatDate value="${match.date}" pattern="dd.MM.yyyy hh:mm"/></td>
                                        <td>${match.awayTeam.name}</td>
                                        <td>${match.homeTeam.name}</td>
                                        <td>${match.awayScore}:${match.homeScore}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
