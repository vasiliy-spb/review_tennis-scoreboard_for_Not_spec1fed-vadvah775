<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Scoreboard | Finished Matches</title>
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
                <a class="nav-link" href="${pageContext.request.contextPath}/matches">Matches</a>
            </nav>
        </div>

    </section>
</header>
<main>
    <div class="container">
        <h1>Matches</h1>

        <form method="get" action="${pageContext.request.contextPath}/matches">
            <div class="input-container">
                <input class="input-filter" placeholder="Filter by player name" type="text"
                       name="filter_by_player_name" value="${filterName}">
                <button type="submit" class="btn-filter">Search</button>
                <button type="button" class="btn-filter" onclick="location.href='${pageContext.request.contextPath}/matches'">Reset Filter</button>
            </div>
        </form>

        <table class="table-matches">
            <tr>
                <th>Player One</th>
                <th>Player Two</th>
                <th>Winner</th>
            </tr>
            <c:forEach var="match" items="${matches}">
                <tr>
                    <td>${match.player1.name}</td>
                    <td>${match.player2.name}</td>
                    <td><span class="winner-name-td">${match.winner.name}</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty matches}">
                <tr><td colspan="3">No matches found.</td></tr>
            </c:if>
        </table>


        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a class="prev" href="${pageContext.request.contextPath}/matches?page=${currentPage-1}&filter_by_player_name=${filterName}"> < </a>
                </c:if>

                <!-- Цикл от 1 до totalPages отображает сразу все существующие страницы. Лучше сделать окно пагинации ограниченным текущей страницей +-2 вокруг неё -->
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <c:choose>
                        <c:when test="${i == currentPage}">
                            <a class="num-page current" href="#">${i}</a>
                        </c:when>
                        <c:otherwise>
                            <a class="num-page" href="${pageContext.request.contextPath}/matches?page=${i}&filter_by_player_name=${filterName}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a class="next" href="${pageContext.request.contextPath}/matches?page=${currentPage+1}&filter_by_player_name=${filterName}"> > </a>
                </c:if>
            </div>
        </c:if>

    </div>
</main>
</body>
</html>
