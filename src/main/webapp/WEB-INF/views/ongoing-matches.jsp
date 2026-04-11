<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Scoreboard | Ongoing Matches</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</head>
<body>
<header class="header">
    <section class="nav-header">
        <div class="brand">
            <div class="nav-toggle">
                <img src="${pageContext.request.contextPath}/images/menu.png" alt="Logo" class="logo">
            </div>
            <span class="logo-text">TennisScoreboard</span>
        </div>
        <div>
            <nav class="nav-links">
                <a class="nav-link" href="${pageContext.request.contextPath}">Home</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/matches">Finished Matches</a>
            </nav>
        </div>
    </section>
</header>
<main>
    <div class="container">
        <h1>Ongoing Matches</h1>

        <c:choose>
            <c:when test="${empty ongoingMatches}">
                <p>No ongoing matches. <a href="${pageContext.request.contextPath}/new-match">Start a new match</a>.</p>
            </c:when>
            <c:otherwise>
                <table class="table-matches">
                    <tr>
                        <th>Player 1</th>
                        <th>Player 2</th>
                        <th>Current Score</th>
                        <th>Action</th>
                    </tr>
                    <c:forEach var="match" items="${ongoingMatches}">
                        <tr>
                            <td>${match.player1.name}</td>
                            <td>${match.player2.name}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${match.tiebreak}">
                                        Tiebreak: ${match.pointsPlayer1} - ${match.pointsPlayer2}
                                    </c:when>
                                    <c:otherwise>
                                        ${match.gamesPlayer1} : ${match.gamesPlayer2}
                                        (<c:choose>
                                        <c:when test="${match.pointsPlayer1 == 0}">0</c:when>
                                        <c:when test="${match.pointsPlayer1 == 1}">15</c:when>
                                        <c:when test="${match.pointsPlayer1 == 2}">30</c:when>
                                        <c:when test="${match.pointsPlayer1 == 3}">40</c:when>
                                        <c:when test="${match.pointsPlayer1 == 4}">AD</c:when>
                                    </c:choose> -
                                        <c:choose>
                                            <c:when test="${match.pointsPlayer2 == 0}">0</c:when>
                                            <c:when test="${match.pointsPlayer2 == 1}">15</c:when>
                                            <c:when test="${match.pointsPlayer2 == 2}">30</c:when>
                                            <c:when test="${match.pointsPlayer2 == 3}">40</c:when>
                                            <c:when test="${match.pointsPlayer2 == 4}">AD</c:when>
                                        </c:choose>)
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <button type="button" class="btn-filter" onclick="location.href='${pageContext.request.contextPath}/match-score?uuid=${match.UUID}'">Continue</button>
                                <form method="post" action="${pageContext.request.contextPath}/delete-ongoing-match"
                                      style="display: inline; margin-left: 5px;"
                                      onsubmit="return confirm('Are you sure you want to delete this ongoing match? It will be lost forever.');">
                                    <input type="hidden" name="uuid" value="${match.UUID}">
                                    <button type="submit" class="btn-filter" style="background-color: #dc3545;">Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</main>
</body>
</html>
