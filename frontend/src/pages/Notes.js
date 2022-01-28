import React, { useState } from "react"

import { NewNote } from "../elements/NewNote"
import { NotesTable } from "../elements/NotesTable"

export function Notes() {
    const [refreshId, setRefreshId] = useState(0)

    return (
        <div>
            <NewNote onCreateNote={() => setRefreshId(id => (id > 100) ? 0 : id + 1)} />
            <hr />
            <NotesTable key={refreshId} />
        </div>
    )
}