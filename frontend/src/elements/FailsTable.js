import React, { useEffect, useState } from 'react'
import { fetchFromApi } from '../util/Fetching'
import { DateText } from './DateText'

export function FailsTable({ }) {
    const loadStep = 50
    const [lines, setLines] = useState([])
    const [lastOffset, setLastOffset] = useState(0)
    const [anyLeft, setAnyLeft] = useState(true)

    const handleLoadNext = () => {
        fetchFromApi("/fail/get?" + "limit=" + loadStep + "&offset=" + lastOffset, {})
            .then(res => res.json().then(fls => {
                if (fls.length > 0) {
                    setLines(prev => prev.concat(fls.filter(ls => ls !== null && ls != undefined)).sort((a, b) => a > b ? -1 : a == b ? 0 : 1))
                    setLastOffset(prev => prev + loadStep)

                    if (fls.length < loadStep) {
                        setAnyLeft(false)
                    }
                } else {
                    setAnyLeft(false)
                }
            }))
    }

    useEffect(() => {
        handleLoadNext()
    }, [])

    return (
        <div>
            <table className="hover unstriped">
                <thead>
                    <tr key="header">
                        <th key="date">Date</th>
                        <th key="reason">Reason</th>
                        <th key="toWhat">Using</th>
                        <th key="prevDayCount">Lost streak</th>
                        <th key="satisfied">Felt satisfied</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        lines.map(line => <FailRow key={line.id} failLine={line} />)
                    }
                </tbody>
            </table>
            <button className="button " onClick={handleLoadNext} disabled={!anyLeft}>Load next {loadStep} fails.</button>
        </div>
    )
}

function FailRow({ failLine }) {
    return (
        <tr key={failLine.id}>
            <td key={failLine.id + "-date"}><DateText date={failLine.date} addTooltip={true} /></td>
            <td key={failLine.id + "-reason"}>{failLine.reason}</td>
            <td key={failLine.id + "-toWhat"}>{failLine.toWhat}</td>
            <td key={failLine.id + "-prevDayCount"}>{failLine.prevDayCount}</td>
            <td key={failLine.id + "-satisfied"}>{(failLine.satisfied) ? "yes" : "no"}</td>
        </tr>
    )
}
