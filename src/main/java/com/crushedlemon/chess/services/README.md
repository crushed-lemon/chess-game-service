<h3>Steps to take when a move is played</h3>
<ol>
    <li>Get the game from gameId </li>
    <li> Authenticate the move 
        <ol>
            <li> The player is playing the game, and it is their turn </li>
            <li> The player has moved their own piece </li>
        </ol>
    </li>
    <li> Try to play the move </li>
    <li>Try to play the move
        <ol>
            <li>Validate the move
                <ol type="a">
                    <li>Piece-wise location validation</li>
                    <li>Assert no pieces were in between</li>
                    <li>Assert not capturing own piece</li>
                    <li>Assert doesn't move king into check</li>
                    <li>If castles or en-passant, assert that it was allowed</li>
                </ol>
            </li>
            <li>Play the move if valid</li>
            <li>Update state</li>
        </ol>
    </li>
    <li>Determine impact of the move
        <ol type="a">
            <li>Nothing</li>
            <li>Capture</li>
            <li>Check</li>
            <li>Checkmate</li>
            <li>Stalemate</li>
        </ol>
    </li>
    <li>Give a name and timestamp to the move</li>
    <li>Register the move in moves db</li>
    <li>Calculate and save the state of the game</li>
    <li>Notify the other player</li>
</ol>