import React, { useEffect, useState } from 'react'
import ReactDOM, { render } from 'react-dom'
import { FailsTable } from './elements/FailsTable'
import { NewFail } from './elements/NewFail'

import './util/Fetching'

function App() {
    const [refreshId, setRefreshId] = useState(0)
    const [appName, setAppName] = useState("Stop it App")

    useEffect(() => {
        fetch("/info/appName").then(res => {
            if (res.ok) {
                res.text().then(name => {
                    if (name !== undefined && name !== null && name.length != 0)
                        setAppName(name)
                })
            }
        })
    }, [])

    useEffect(() => {
        document.title = appName
    }, [appName])

    return (
        <div className="grid-container align-center-middle">
            <div className="text-center top-header">
                <h3>{appName}</h3>
            </div>
            <div style={ {margin: "1em auto auto"} }>
                <NewFail onFailReport={() => setRefreshId(id => (id > 100) ? 0 : id + 1)} />
                <hr />
                <FailsTable key={refreshId} />
            </div>
        </div>
    )
}

ReactDOM.render(<App />, document.getElementById("app"))
