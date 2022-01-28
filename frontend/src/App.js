import React, { useEffect, useState } from 'react'
import ReactDOM from 'react-dom'
import { TopBar } from './elements/TopBar'
import { Fails } from './pages/Fails'
import { Notes } from './pages/Notes'

import './util/Fetching'

function App() {
    const [loaded, setLoaded] = useState(false)
    const [appName, setAppName] = useState("Stop it app")
    const [lastFailDate, setLastFailDate] = useState(null)
    const [notesOn, setNotesOn] = useState(false)

    const [page, setPage] = useState("fail")

    useEffect(() => {
        fetch("/info/full").then(res => {
            if (res.ok) {
                res.json().then(info => {
                    if (info !== undefined && info !== null) {
                        setAppName(info.appName)
                        setLastFailDate(info.lastFailDate)
                        setNotesOn(info.notesOn)
                        setLoaded(true)
                    }
                })
            }
        })
    }, [])

    useEffect(() => {
        document.title = appName
    }, [appName])

    return (
        <div className="grid-container align-center-middle">
            {
                !loaded ? (<div> Loading </div>) :
                    (
                        <div id="app">
                            <TopBar appName={appName}
                                lastFailDate={lastFailDate} notesOn={notesOn}
                                onChangeToFail={ () => setPage("fail") }
                                onChangeToNotes={ () => setPage("note") }
                            />
                            <div style={{ margin: "1em auto auto" }}>
                                { pageOf(page) }
                            </div>
                        </div>
                    )
            }
        </div>
    )
}

function pageOf(page) {
    switch (page) {
        case "fail": return <Fails />
        case "note": return <Notes />
    }
}

ReactDOM.render(<App />, document.getElementById("app"))
