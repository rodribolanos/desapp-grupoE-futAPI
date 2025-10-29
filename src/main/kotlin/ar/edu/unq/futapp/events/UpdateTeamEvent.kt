package ar.edu.unq.futapp.events

import org.springframework.context.ApplicationEvent

class UpdateTeamEvent(val name: String, source: Any): ApplicationEvent(source)