// Format time
import { dayjs } from 'element-plus'

export const formatTime = (time) => dayjs(time).format('MMM D, YYYY')
