import React, { useState } from 'react'
import { FailsTable } from '../elements/FailsTable'
import { NewFail } from '../elements/NewFail'

export function Fails() {
    const [refreshId, setRefreshId] = useState(0)

    return (
        <div>
            <NewFail onFailReport={() => setRefreshId(id => (id > 100) ? 0 : id + 1)} />
            <hr />
            <FailsTable key={refreshId} />
        </div>
    )
}