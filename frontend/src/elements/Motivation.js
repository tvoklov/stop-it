import moment from "moment"
import React from "react"

export function Motivation({ lastFail }) {
    const dateM = moment(lastFail)

    return (
        <span> {toMotivation(moment().diff(dateM, "days"))} </span>
    )
}

const dayCountToUnit = [
    [7, "week"],
    [31, "month"],
    [365, "year"]
]

function formSentence(dCount, [count, unit]) {
    if (dCount == 0) {
        return "First day of your 1 " + unit + " streak"
    } else if (dCount % count == 0) {
        return "Good job! You hit a " + dCount / count + " " + unit + " streak!"
    } else {
        const beforeUnitStreak = count - (dCount % count)
        const unitStreakCount = dCount == 0 ? 1 : dCount == count ? 2 : Math.ceil(dCount / count)

        return "Only " + beforeUnitStreak +
            " more " + (beforeUnitStreak == 1 ? "day" : "days") +
            " before a " + unitStreakCount + " " + unit + " streak"
    }
}

function toMotivation(dCount) {
    function go(tg) {
        if (tg.length >= 2) {
            const curr = tg[0]
            const next = tg[1]

            if (dCount >= next[0]) return go(tg.shift())
            else return formSentence(dCount, curr)
        } else if (tg.length == 1) {
            return formSentence(dCount, tg[0])
        } else return "Err"
    }

    return go(dayCountToUnit)
}
