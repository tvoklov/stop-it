import moment from 'moment'
import React from 'react'
import { DateText } from './DateText'
import { Motivation } from './Motivation'

export function TopBar({ appName, lastFailDate, notesOn, onChangeToFail, onChangeToNotes }) {

    const lastFailLine = Math.abs(moment().diff(moment(lastFailDate), "hours")) > 12 ?
        (<DateText date={lastFailDate} addTooltip={true} toReadable={m => m.fromNow()} />) :
        (<DateText date={lastFailDate} addTooltip={true} toReadable={() => "recently"} />)

    const menuItems = notesOn ? (<>
        <div className="v-separator" style={{ paddingRight: "1rem" }}></div>
        <li className="align-self-middle"><h4> <a onClick={ onChangeToFail }>Fails</a> </h4> </li>
        <li className="align-self-middle"><h4> <a onClick={ onChangeToNotes }>Notes</a> </h4></li>
    </>) : (<></>)

    return (
        <div className="top-bar">
            <div className="top-bar-left">
                <ul className="horizontal menu">
                    <li className="menu-text align-self-middle"> <h3> {appName} </h3></li>
                    {menuItems}
                </ul>
            </div>

            <div className="top-bar-right" style={{ textAlign: "right" }}>
                <ul className="vertical menu">
                    <li className="menu-text"> Last fail: {lastFailLine} </li>
                    <li className="menu-text"> <Motivation lastFail={lastFailDate} /> </li>
                </ul>
            </div>
        </div>
    )
}