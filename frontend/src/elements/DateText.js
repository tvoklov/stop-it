import React from 'react'
import moment from 'moment'

export function DateText({ date, format, addTooltip, tooltipFormat, toReadable }) {
    const dateM = moment(date)
    const format_ = format ? format : "DD.MM.yyyy"

    const tooltip = addTooltip ? makeTooltip(tooltipFormat, dateM) : ({})

    const readable = toReadable ? toReadable(dateM) : dateM.format(format_)

    return (
        <span {...tooltip} > { readable } </span>
    )
}

function makeTooltip(format, date) {
    const format_ = format ? format : "DD.MM.yyyy HH:mm:ss"
    return {
        title: date.format(format_)
    }
}