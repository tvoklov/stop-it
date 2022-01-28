import React, { useEffect, useState } from 'react'
import { fetchFromApi } from '../util/Fetching'
import { DateText } from './DateText'

export function NotesTable() {
    const loadStep = 50
    const [lines, setLines] = useState([])
    const [lastOffset, setLastOffset] = useState(0)
    const [anyLeft, setAnyLeft] = useState(true)
    
    const handleLoadNext = () => {
        fetchFromApi("/note/get?" + "limit=" + loadStep + "&offset=" + lastOffset, {})
            .then(res => res.json().then(nls => {
                if (nls.length > 0) {
                    setLines(prev => prev.concat(nls.filter(ls => ls !== null && ls != undefined)).sort((a, b) => a > b ? -1 : a == b ? 0 : 1))
                    setLastOffset(prev => prev + loadStep)

                    if (nls.length < loadStep) {
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
                        <th key="note">Note</th>
                        <th key="dayCount">Since last fail</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        lines.map(line => <NoteRow key={line.id} noteLine={line} />)
                    }
                </tbody>
            </table>
            <button className="button " onClick={handleLoadNext} disabled={!anyLeft}>Load next {loadStep} notes.</button>
        </div>
    )
}

function NoteRow({ noteLine }) {
    return (
        <tr key={noteLine.id}>
            <td key={noteLine.id + "-date"}><DateText date={noteLine.date} addTooltip={true} /></td>
            <td key={noteLine.id + "-note"}>{noteLine.note}</td>
            <td key={noteLine.id + "-dayCount"}>{noteLine.dayCount}</td>
        </tr>
    )
}
