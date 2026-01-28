<#macro footer users>
    <footer class="container">
        <details>
            <summary role="button" class="outline secondary">List Accounts</summary>
            <table>
                <thead>
                    <tr>
                        <th scope="col">Username</th>
                        <th scope="col">Password</th>
                    </tr>
                </thead>
                <tbody>
                    <#list users as username, password>
                        <tr>
                            <td>
                                ${username}
                            </td>
                            <td>
                                ${password}
                            </td>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </details>
    </footer>
</#macro>