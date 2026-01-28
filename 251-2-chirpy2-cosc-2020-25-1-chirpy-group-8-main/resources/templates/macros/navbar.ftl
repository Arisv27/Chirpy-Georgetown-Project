<#macro navbar username>
    <nav>
        <ul>
            <li><strong>Chirpy</strong></li>
        </ul>
        <ul>
            <li><a href="/timeline/">Discover</a></li>
            <li><a href="/followtimeline/">Following</a></li>
            <li><a href="/postchirp/">Post</a></li>
            <li><a href="/search/">Search</a></li>
            <li>
                <details class="dropdown">
                    <summary>
                        ${username}
                    </summary>
                    <ul>
                        <li><a href="/logout/">Log Out</a></li>
                    </ul>
                </details>
            </li>
        </ul>
    </nav>
</#macro>