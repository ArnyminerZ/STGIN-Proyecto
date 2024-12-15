export function getMatchId() {
    return sessionStorage.getItem("matchId");
}

export function setMatchId(matchId) {
    sessionStorage.setItem("matchId", matchId);
}
