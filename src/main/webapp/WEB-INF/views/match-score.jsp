<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Scoreboard | Match Score</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto+Mono:wght@300&display=swap" rel="stylesheet">
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
                <a class="nav-link" href="${pageContext.request.contextPath}/matches">Matches</a>
            </nav>
        </div>
    </section>
</header>
<main>
    <div class="container">
        <h1>Current match</h1>
        <div class="current-match-image"></div>
        <section class="score">
            <table class="table">
                <thead class="result">
                <tr>
                    <th class="table-text">Player</th>
                    <th class="table-text">Sets</th>
                    <th class="table-text">Games</th>
                    <th class="table-text">Points</th>
                </tr>
                </thead>
                <tbody>
                <tr class="player1">
                    <td class="table-text">${match.player1.name}</td>
                    <td class="table-text">${match.setsPlayer1}</td>
                    <td class="table-text">${match.gamesPlayer1}</td>
                    <!-- JSP страница не должна содержать логику преобразования счёта — это задача маппера или сервисного слоя. -->
                    <td class="table-text">
                        <c:choose>
                            <c:when test="${match.tiebreak}">${match.pointsPlayer1}</c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${match.pointsPlayer1 == 0}">0</c:when>
                                    <c:when test="${match.pointsPlayer1 == 1}">15</c:when>
                                    <c:when test="${match.pointsPlayer1 == 2}">30</c:when>
                                    <c:when test="${match.pointsPlayer1 == 3}">40</c:when>
                                    <c:when test="${match.pointsPlayer1 == 4}">AD</c:when>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="table-text">
                        <form method="post" action="${pageContext.request.contextPath}/match-score">
                            <input type="hidden" name="uuid" value="${match.UUID}">
                            <input type="hidden" name="winnerId" value="${match.player1.id}">
                            <button type="submit" class="score-btn">Score</button>
                        </form>
                    </td>
                </tr>
                <tr class="player2">
                    <td class="table-text">${match.player2.name}</td>
                    <td class="table-text">${match.setsPlayer2}</td>
                    <td class="table-text">${match.gamesPlayer2}</td>
                    <td class="table-text">
                        <c:choose>
                            <c:when test="${match.tiebreak}">${match.pointsPlayer2}</c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${match.pointsPlayer2 == 0}">0</c:when>
                                    <c:when test="${match.pointsPlayer2 == 1}">15</c:when>
                                    <c:when test="${match.pointsPlayer2 == 2}">30</c:when>
                                    <c:when test="${match.pointsPlayer2 == 3}">40</c:when>
                                    <c:when test="${match.pointsPlayer2 == 4}">AD</c:when>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="table-text">
                        <form method="post" action="${pageContext.request.contextPath}/match-score">
                            <input type="hidden" name="uuid" value="${match.UUID}">
                            <input type="hidden" name="winnerId" value="${match.player2.id}">
                            <button type="submit" class="score-btn">Score</button>
                        </form>
                    </td>
                </tr>
                </tbody>
            </table>
        </section>
    </div>
</main>
</body>
</html>
