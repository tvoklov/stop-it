import React, { useState } from "react"
import { postToApi } from "../util/Fetching"

export function NewNote({ onCreateNote }) {
    const [note, setNote] = useState({})

    const handleCreateNote = () => {
        postToApi("/note/new", note, {}).then(() => {
            setNote({})
            onCreateNote()
        })
    }

    return (
        <div>
            <form>
                <div className="grid-container">
                    <section className="medium-6 cell">
                        <label htmlFor="note">Note</label>
                        <input
                            type="text" id="note" name="note"
                            value={note.note ? note.note : ""}
                            onChange={({ target }) => setNote(p => ({ ...p, note: target.value }))}
                        />
                    </section>

                    <button className="submit success button" type="button" onClick={handleCreateNote}>Add note</button>
                </div>
            </form>
        </div>
    )
}