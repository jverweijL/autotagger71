<%@ include file="init.jsp" %>
<h1>hello autotagger management</h1>
${ping}

get rules (${count})

<c:forEach items="${rules}" var="rule">
    Key = ${rule.key}, value = ${rule.value}<br>
</c:forEach>