import React from 'react'
import moment from 'moment'

export function DateText({ date, format, addTooltip, tooltipFormat }) {
    const dateM = moment(date)
    const format_ = format ? format : "DD.MM.yyyy"

    const tooltip = addTooltip ? makeTooltip(tooltipFormat, dateM) : ({})

    return (
        <span {...tooltip} > { dateM.format(format_) } </span>
    )
}

function makeTooltip(format, date) {
    const format_ = format ? format : "DD.MM.yyyy HH:mm:ss"
    return {
        title: date.format(format_)
    }
}