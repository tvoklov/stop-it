import React, { useEffect, useState } from 'react'
import ReactDOM from 'react-dom'
import { FailsTable } from './elements/FailsTable'
import { NewFail } from './elements/NewFail'
import { TopBar } from './elements/TopBar'

import './util/Fetching'

function App() {
    const [refreshId, setRefreshId] = useState(0)
    const [loaded, setLoaded] = useState(false)
    const [appName, setAppName] = useState("Stop it app")
    const [lastFailDate, setLastFailDate] = useState(null)

    useEffect(() => {
        fetch("/info/full").then(res => {
            if (res.ok) {
                res.json().then(info => {
                    if (info !== undefined && info !== null) {
                        setAppName(info.appName)
                        setLastFailDate(info.lastFailDate)
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
                            <TopBar appName={appName} lastFailDate={lastFailDate} />
                            <div style={{ margin: "1em auto auto" }}>
                                <NewFail onFailReport={() => setRefreshId(id => (id > 100) ? 0 : id + 1)} />
                                <hr />
                                <FailsTable key={refreshId} />
                            </div>
                        </div>
                    )
            }
        </div>
    )
}

ReactDOM.render(<App />, document.getElementById("app"))
